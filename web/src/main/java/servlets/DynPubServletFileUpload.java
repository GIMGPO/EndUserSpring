package servlets;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.core.UserBean;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.ToolKit;
import trisoftdp.db.TriSoftDb;
import trisoftdp.processing.Publisher;
import trisoftdp.processing.PublisherImpl;
import trisoftdp.web.core.WebMementoUserBean;
import trisoftdp.web.db.TriSoftDbHelper;
import trisoftdp.web.ejb.client.EJBPublisher;

import com.oreilly.servlet.MultipartRequest;


public class DynPubServletFileUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		HttpSession session = req.getSession();
		ProdEnvBean prodEnv = (ProdEnvBean) session.getAttribute("prodEnv");                               
        if (prodEnv == null) { 
        	throw new ServletException ("prodEnv bean is not set");
        }
		MultipartRequest multi = new MultipartRequest(req, CoreConstants.appPropsMap.get("TMP_DIR"),75*1024*1024);
		CoreConstants.logger.info("STATIC PDF UPLOAD: ");
		CoreConstants.logger.info("Params: ");
		Enumeration<String> params = multi.getParameterNames();
		String filename = "";
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			String value = multi.getParameter(name);
			req.setAttribute(name, value);
			CoreConstants.logger.info(name + " = " + value + "; ");
		}
		Enumeration<String> files = multi.getFileNames();
		while (files.hasMoreElements()) {
			String name = files.nextElement();
			filename = multi.getFilesystemName(name);
			String type = multi.getContentType(name);
			File f = multi.getFile(name);
			CoreConstants.logger.info("name: " + name + "; filename: " + filename + "; type: " + type);
			if (f != null) {
				String len = ToolKit.humanByteCount(f.length(), true);
				req.getSession().setAttribute("pdfFileSize", len);
				log("length: " + len);
				CoreConstants.logger.info("length: " + len);
			} else {
				log(filename + " has NO CONTENT");
				CoreConstants.logger.info(filename + " has NO CONTENT");
			}	
			req.getSession().setAttribute("pdfFileName", filename);
		}
		//processing
		long currentId = -1;
		try {
			//the currentId generated together with request	and is taken from the session
			currentId = Long.parseLong((String) req.getSession().getAttribute("currentId")); 
		} catch(NumberFormatException e) {e.printStackTrace();}
		if(currentId < 0)
			throw new ServletException("currentId is not found in the session!");
		CoreConstants.logger.info("currentId=" + currentId);

		//the userPack and pubLegend are taken from the session
		WebMementoUserBean user =  (WebMementoUserBean) req.getSession().getAttribute("user");
		if(user == null || user.getUserPack() == null)
			throw new ServletException("No user bean or pack found in the session!");

		/* moved to publisher.processStatic
		
		//Chosen fake ditaMap is used for the future file name
		String ditaMap = "";
		ditaMap = (user.getUserPack().ditaMaps[0].title != null)? user.getUserPack().ditaMaps[0].title : "generic_nogood";
		CoreConstants.logger.info("Future file name (ditaMap): " + ditaMap + ".pdf");
		 */
		
		//Publisher publisher = new PublisherImpl();
		Publisher publisher = EJBPublisher.getThePublisher();

		String uploadFile = filename;
		String lang = (String) req.getSession().getAttribute("lang");
		File uploadedFile = new File(uploadFile);
		if (lang == null) lang="en_US";
		CoreConstants.logger.info("Language: " + lang);
		TriSoftDb db = null;
		if(user.getPubLegend() == null)
			throw new ServletException("No pubLegend found in the session!");
		try {
			publisher.processStatic(currentId, user, prodEnv, lang, uploadedFile, "yes".equals(prodEnv.getProdCleanAfter()));
			/* moved to publisher.processStatic
			File tarDir = new File(publisher.targetDir(currentId, ditaMap),"output");
			File src = ToolKit.getFileById(currentId,tarDir);
			CoreConstants.logger.info("src=" + src);
			File trgt = new File(CoreConstants.appPropsMap.get("RESULT_DIR") + File.separator + src.getName());		
			CoreConstants.logger.info("trgt=" + trgt);
			CoreConstants.logger.info("Copying " + src.toString() + " to " + trgt.toString());
			ToolKit.copyDirectory(src,trgt);*/

			// write to the db
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			db.saveResult(currentId, ToolKit.getMD5(user.getUserPack()), user.getUserPack(), null);
			db.markRecord(currentId, "generic");
			CoreConstants.logger.info("SUCCESS: Saved request to db with Id=" + currentId + ", it was marked as generic.");
		} catch (DynException e) {
			throw new ServletException("Processing failed:  DynException:" + e.getMessage());
		} catch (SQLException e) {
			CoreConstants.logger.severe("SQLException:" + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {} 
		}

/* moved to publisher.processStatic
		//Clean up after publishing and saving the result
		if("yes".equals(prodEnv.getProdCleanAfter())) {
			CoreConstants.logger.info("Cleaning up after publishing...");
			File targetDir = publisher.targetDir(currentId, ditaMap);
			ToolKit.deleteDir(targetDir);
			File tmpFile = new File(CoreConstants.appPropsMap.get("TMP_DIR") + File.separator + uploadedFile);
			if (tmpFile.exists()) 
				if(!tmpFile.delete())
					throw new ServletException("Filed to delete " + tmpFile);
		}
		*/
		//Redirecting
		res.sendRedirect("DynDispatcher?state=uploadComplete");
	}

}

