package trisoftdp.web.processing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.core.DynMail;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.ToolKit;
import trisoftdp.db.TriSoftDb;
import trisoftdp.processing.Publisher;
import trisoftdp.web.db.TriSoftDbHelper;
import trisoftdp.web.ejb.client.EJBPublisher;
import trisoftdp.core.DynPubNotifications;
import trisoftdp.core.UserBean;
import trisoftdp.core.ProdEnvBean;

public class DynPubJob implements Runnable {

	private Logger logger = CoreConstants.logger;
	private Map<String,String> appStringsMap;
	private String lang;
	private ProdEnvBean prodEnv;
	private UserBean user;
	private String uploadedFilePath = null;
	private long staticId = -1;
	private boolean cleanup;

	public DynPubJob(UserBean user, ProdEnvBean prodEnv, Map<String,String> appStringsMap, String lang) throws CloneNotSupportedException, IOException, DynException {
		init(user, prodEnv, appStringsMap, lang); 
	}
	public DynPubJob(long staticId, UserBean user, ProdEnvBean prodEnv, Map<String,String> appStringsMap, String lang, String uploadedFilePath, boolean cleanup) throws CloneNotSupportedException, IOException, DynException {
		init(user, prodEnv, appStringsMap, lang);
		this.staticId = staticId;
		this.uploadedFilePath = uploadedFilePath;
		this.cleanup = cleanup;
	}
	
	public void run() {
		if(uploadedFilePath != null && staticId != -1) 
			runStatic();
		else 
			runDynamic();
	}
	
	
	
	public void runDynamic() {
		long oldId, id = -1;
		TriSoftDb db = null;
		//ApplicationContext context = new ClassPathXmlApplicationContext("rmiClientAppContext.xml");		
		//Remote User Service is called via RMI Client Application Context...
		//RemotePublisher remotePublisher = (RemotePublisher) context.getBean("RemotePublisher");
		Publisher remotePublisher = EJBPublisher.getThePublisher();
		DynamicPublishingPackage pack = user.getUserPack();
		String[] emails = user.getUserEmail().replaceAll("\\s+", "").split(";");	
		String md5 = null;
		try {
			md5 = ToolKit.getMD5(pack);
			db = new TriSoftDbHelper();
			logger.info("Dynamic processing started");			
			oldId = db.getResultId(md5);
			//TODO do not forget to remove the pack info
			String logMsg = "Request pack:\n" + ToolKit.printRequest(pack) + "\nUser e-mail: ";
			for(String email: emails)
				logMsg = logMsg.concat(email + ", ");
			logger.info(logMsg);
			if(oldId > 0) {
				logger.info("Request has been processed before. md5=" + md5);
				id = oldId;
			}
			else 
				id = remotePublisher.process(user, prodEnv, lang);
			logger.info("Processing finished with returned id=" + id);
			File rf = ToolKit.getResultById(id);
			if(rf == null)
				throw new DynException("Result file not found for resutlId=" + id);
			synchronized(DynPubJob.class) {
				oldId = db.getResultId(md5);
				if(oldId > 0) {
					//TODO clean the directory associated with the id	(must return immediately)			
					id = oldId;
				}
				else
					db.saveResult(id, md5, pack, null);
			}
			if(pack.pubPackage.equals("generic")) {
				db.markRecord(id, "generic");
				logger.info("Saved result was marked as generic");
			}
			logger.info("Saved result in the Database");
			String[] recipients = emails;
			StringBuilder buf = new StringBuilder();
			buf.append("<html><body>\n");
			buf.append("<div style = 'font-family: Arial; font-size: 15px; font-weight: bold; color:#095a90; display:block; border-bottom: 2px solid #095a90; padding-bottom:10px; margin:20px 0 20px 0;'>\n");
			buf.append(appStringsMap.get("email.title"));
			buf.append("</div>\n");
			buf.append("<div style = 'font-family: Verdana; font-size: 12px; line-height: 16px;'>\n");
			buf.append(DynPubNotifications.getCompleteMessage(id, pack, appStringsMap, prodEnv));
			buf.append("</div>\n");
			buf.append("<div style = 'font-family: Verdana; font-size: 9px; color:gray; display:block; border-top: 1px solid gray; padding-top:5px; margin:20px 0 20px 0; text-align:right;'>\n");
			buf.append("&copy;" + appStringsMap.get("email.copyright") + "\n");
			buf.append("</div>\n");
			buf.append("</body></html>");
			String title = (pack.ditaMaps[0].task != null)? pack.ditaMaps[0].task : pack.ditaMaps[0].title;
			DynMail.postMail(recipients, appStringsMap.get("email.docAvailable") + ": " + title, buf.toString(), prodEnv.getProdSupportEmail(), "text/html; charset=utf-8");
			logger.info("The notification to " + user.getUserEmail() + " was sent...");
		} catch (DynException e) {
			logger.severe("DynExcepsion: " + e.getMessage());
			if(db != null) {
				String note = String.format("email: %s%nconfigId: %s%nDynException: %s", user.getUserEmail(), user.getConfigId(), e.getMessage());
				try {
					db.addFailedJob(note, md5, pack);
				} catch (SQLException e1) {
					logger.severe("SQLException: " + e1.getMessage() + "\nFailed to save a failed job with the note: " + note);
				}
			}
		} catch (IOException e) {
			logger.severe("IOException: " + e.getMessage());
		} catch (MessagingException e) {
			logger.severe("MessagingException: " + e.getMessage());
		} catch (SQLException e) {
			logger.severe("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch (Exception e) {}
		}
		
		
	}

	
	private void init(UserBean user, ProdEnvBean prodEnv, Map<String,String> appStringsMap, String lang) throws CloneNotSupportedException, IOException, DynException{ 
		this.appStringsMap = appStringsMap;
		this.lang = lang;
		
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		byte[] obj = null;
		//user
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(user);
		oos.close();
		obj = baos.toByteArray();
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(obj));
		try {
			this.user = (UserBean) in.readObject();		
			in.close();
		//prodEnv
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(prodEnv);
			oos.close();
			obj = baos.toByteArray();
			in = new ObjectInputStream(new ByteArrayInputStream(obj));
			this.prodEnv = (ProdEnvBean) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DynException("DynPubJob.init(...) serialization/deserialization problem: " + e.getMessage());
		}
		in.close();
	}
	
	public void runStatic() {
		logger.info("Static processing started");
		long id = -1;
		TriSoftDb db = null;
		Publisher remotePublisher = EJBPublisher.getThePublisher();
		DynamicPublishingPackage pack = user.getUserPack();	
		String md5 = null;
		try {
			md5 = ToolKit.getMD5(pack);
			db = new TriSoftDbHelper();
			remotePublisher.processStatic(staticId, user, prodEnv, md5, uploadedFilePath, cleanup);
			logger.info("Processing finished with returned id=" + id);
			db.saveResult(id, md5, pack, null);	
			db.markRecord(id, "generic");
			logger.info("Saved result was marked as generic");
		} catch (DynException e) {
			logger.severe("DynExcepsion: " + e.getMessage());
			if(db != null) {
				String note = String.format("generic configId: %s%nDynException: %s", user.getConfigId(), e.getMessage());
				try {
					db.addFailedJob(note, md5, pack);
				} catch (SQLException e1) {
					logger.severe("SQLException: " + e1.getMessage() + "\nFailed to save a failed job with the note: " + note);
				}
			}
		} catch (IOException e) {
			logger.severe("IOException: " + e.getMessage());
		}  catch (SQLException e) {
			logger.severe("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch (Exception e) {}
		}		
	}
}
