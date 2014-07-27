package servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DownloadAllRelDocs;
import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.ProdEnvBean;

/**
 * Servlet implementation class DownloadRelDocServlet
 */
public class DownloadRelDocServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadRelDocServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		ServletOutputStream stream = null;
		BufferedInputStream buf = null;
		DynamicPublishingPackage.OUTPUT_TYPE outputType = DynamicPublishingPackage.OUTPUT_TYPE.pdf2;
		// HttpSession session = req.getSession(true);
		// String lang = CoreConstants.languagesMap.get(session.getAttribute("lang"));
		String lang = (String) req.getSession().getAttribute("lang");
		String prod = (String) req.getParameter("prod");
		ProdEnvBean prodEnv = new ProdEnvBean();
		try {
			prodEnv = new ProdEnvBean(prod, lang);
		} catch (DynException e) {
			throw new ServletException("DynException: " + e.getMessage());
		}
		String fileName = "";
		String fileFullName = "";
		String relDocsXmlFileName = "";
		FileInputStream is = null;
		FileInputStream input = null;
		File rf;
		try {
			fileName = req.getParameter("file");
			relDocsXmlFileName = req.getParameter("xmlFile");
			// Added by Chaya
			if ("all".equals(fileName)) {
				String zipfileName = (String) req.getSession().getAttribute("rdTitle");
				zipfileName = zipfileName.replaceAll("[^a-z,A-Z,0-9]", "_").replaceAll("__*","_") + "_" 
						+ CoreConstants.languagesMap.get(lang) + ".zip";
				
				String zipfileFullName = prodEnv.getProdRelDocsCacheDir() + zipfileName;
				rf = new File(zipfileFullName);
				if (!rf.exists()) {				
					DownloadAllRelDocs d = new DownloadAllRelDocs();
					String relDocsXmlPath = prodEnv.getProdConfigDir() + relDocsXmlFileName + ".xml";
					CoreConstants.logger.info("relDocsXmlPath = " + relDocsXmlPath);
					List<String> filesToDownload = d.parseRelatedDocsXml(relDocsXmlPath);
					CoreConstants.logger.info("# of files to be zipped: " + filesToDownload.size());
					d.zipFiles(filesToDownload, zipfileName, prodEnv.getProdRelDocsCacheDir(), prodEnv.getProdRelDocsDir());
					CoreConstants.logger.info("Created zip file");
				}
				
			} 	else {
				fileFullName = prodEnv.getProdRelDocsDir() + fileName;
				rf = new File(fileFullName);
			}
				// Added by Chaya
			if (fileName.length() < 0)
				throw new ServletException("File name is not specified");
			if (fileName.endsWith(".zip"))
				outputType = DynamicPublishingPackage.OUTPUT_TYPE.xhtml;
			for (Cookie c : req.getCookies()) {
				CoreConstants.logger.info(c.getName() + " = " + c.getValue());
				if ("appLang".equals(c.getName())) {
					lang = CoreConstants.languagesMap.get(c.getValue());
					break;
				}
			}
				// String fileFullName = prodEnv.getProdRelDocsDir() + fileName;
				// take from file
			switch (outputType) {
			case pdf2:
				resp.setContentType("application/pdf");
				break;
			case xhtml:
				resp.setContentType("multipart/x-zip");
				break;
			default:
				resp.setContentType("application/pdf");
			}
			if (!rf.exists())
				throw new ServletException("File does not exist: " + rf);
			resp.addHeader("Content-Disposition", "attachment; filename="+ rf.getName());
			resp.setContentLength((int) rf.length());
			input = new FileInputStream(rf);
			buf = new BufferedInputStream(input);
			stream = resp.getOutputStream();
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1)
				stream.write(readBytes);
			if (stream != null)
				stream.close();
			if (buf != null)
				buf.close();
		} finally {
			if (buf != null)	try {buf.close();} catch (IOException e) {e.printStackTrace();}
			if (is != null)	try {is.close();} catch (IOException e) {e.printStackTrace();}
			if (input != null)	try {input.close();} catch (IOException e) {e.printStackTrace();}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}

}
