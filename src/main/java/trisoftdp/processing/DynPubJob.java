package trisoftdp.processing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.core.DynMail;
import trisoftdp.core.DynPubNotifications;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.ToolKit;
import trisoftdp.db.TriSoftDb;
//import trisoftdp.processing.Publisher;
//import trisoftdp.processing.PublisherImpl;
import trisoftdp.rmi.server.RemotePublisher;

public class DynPubJob implements Runnable {

	private final Logger logger = CoreConstants.logger;
	private final Map<String,String> appStringsMap;
	private final String lang;
	private final ProdEnvBean prodEnv;
	private final MementoUserBean user;
//	private final DynamicPublishingPackage pack;

	public DynPubJob (MementoUserBean user, ProdEnvBean prodEnv, Map<String,String> appStringsMap, String lang) throws CloneNotSupportedException, IOException, ClassNotFoundException {
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
		this.user = (MementoUserBean) in.readObject();
		in.close();
		//prodEnv
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(prodEnv);
		oos.close();
		obj = baos.toByteArray();
		in = new ObjectInputStream(new ByteArrayInputStream(obj));
		this.prodEnv = (ProdEnvBean) in.readObject();
		in.close();
	}
	
	public void run() {
		long oldId, id = -1;
		TriSoftDb db = null;
		ApplicationContext context = new ClassPathXmlApplicationContext("rmiClientAppContext.xml");		
		//Remote User Service is called via RMI Client Application Context...
		RemotePublisher remotePublisher = (RemotePublisher) context.getBean("RemotePublisher");
		DynamicPublishingPackage pack = user.getUserPack();
		String[] emails = user.getUserEmail().replaceAll("\\s+", "").split(";");	
		String md5 = null;
		try {
			md5 = ToolKit.getMD5(pack);
			db = ToolKit.newDB();
			logger.info("Processing started");			
			//id = publisher.process(configId, contentFolder, configFolder, pack, legend, lang, prodEnv);
			oldId = db.getResultId(md5);
			//TODO do not forget to remove the pack info
			logger.info("Request pack:\n" + ToolKit.printRequest(pack) + "\nUser e-mail: " + emails);
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
			//String[] recipients = ToolKit.joinArrays(emails,supportEmails);
			String[] recipients = emails;
			StringBuilder buf = new StringBuilder();
			buf.append("<html><body>\n");
			buf.append("<div style = 'font-family: Arial; font-size: 15px; font-weight: bold; color:#095a90; display:block; border-bottom: 2px solid #095a90; padding-bottom:10px; margin:20px 0 20px 0;'>\n");
			//buf.append(Constants.appStringsMap.get("email.automatedMail"));
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
			if(context != null) ((ClassPathXmlApplicationContext) context).close();
		}
		
		
	}

}
