package servlets;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.ToolKit;
import trisoftdp.db.TriSoftDb;
import trisoftdp.web.core.WebMementoUserBean;
import trisoftdp.web.processing.DynPubJob;
import trisoftdp.web.processing.DynPubThreadPoolExecutor;

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
		String filename = null;
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			String value = multi.getParameter(name);
			req.setAttribute(name, value);
			CoreConstants.logger.info(name + " = " + value + "; ");
		}
		Enumeration<String> files = multi.getFileNames();
		if (files.hasMoreElements()) {
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
		String lang = (String) req.getSession().getAttribute("lang");
		if (lang == null) lang="en_US";
		CoreConstants.logger.info("Language: " + lang);
		TriSoftDb db = null;
		if(user.getPubLegend() == null)
			throw new ServletException("No pubLegend found in the session!");
		try {
			DynPubJob job = new DynPubJob(currentId, user.extractUserBean(), prodEnv, null, lang,  filename, "yes".equals(prodEnv.getProdCleanAfter()));
			Future<String> future = DynPubThreadPoolExecutor.getExecutor().submit(job);
			long timeout = 10/*min*/ * 60 /*sec*/ * 1000 /*millisec*/;
			long step = 5*1000;
			long ellapsed = 0;
			while (!future.isDone() && ellapsed < timeout) {
				Thread.sleep(step);
				ellapsed += step; 
			}
			if(ellapsed >= timeout) {
				CoreConstants.logger.severe("Timeout happned. Ellapced " + ellapsed + "milliseconds");
				throw new ServletException("Timeout happned." + ellapsed + "milliseconds");
			}
		} catch (DynException e) {
			throw new ServletException("Processing failed:  DynException:" + e.getMessage());
		} catch (CloneNotSupportedException e) {
			CoreConstants.logger.severe("CloneNotSupportedException:" + e.getMessage());
			throw new ServletException("Processing failed:  CloneNotSupportedException:" + e.getMessage());
		} catch (InterruptedException e) {
			CoreConstants.logger.severe("InterruptedException:" + e.getMessage());
			throw new ServletException("Processing failed:  InterruptedException:" + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {} 
		}
		//Redirecting
		res.sendRedirect("DynDispatcher?state=uploadComplete");
	}

}

