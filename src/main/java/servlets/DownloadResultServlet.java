package servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.ToolKit;



/**
 * Servlet implementation class DownloadResultServlet
 */
public class DownloadResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadResultServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletOutputStream stream = null;
		BufferedInputStream buf = null;
		FileInputStream input = null;
		long resultId = -1;
		DynamicPublishingPackage.OUTPUT_TYPE outputType = null;
		try {
			resultId = Long.parseLong(req.getParameter("docId"));
			//The default output type is pdf
			outputType = DynamicPublishingPackage.OUTPUT_TYPE.valueOf("pdf2");
			if (req.getParameter("outputType")!= "pdf2")
				outputType = DynamicPublishingPackage.OUTPUT_TYPE.valueOf(req.getParameter("outputType"));
		} catch (Exception e) {}
		if(resultId < 0)
			throw new ServletException("resultId not specified");

		//take from file
		switch (outputType){
		case pdf2:
			resp.setContentType("application/pdf");
			break;
		case xhtml:
			resp.setContentType("multipart/x-zip");
			break;
		default:
			resp.setContentType("application/pdf");
		}
		File rf = ToolKit.getResultById(resultId);
		if(rf == null || !rf.exists())
			throw new ServletException("file not found for resultId " + resultId);
		resp.addHeader("Content-Disposition", "attachment; filename=" + rf.getName().substring(1 + rf.getName().indexOf("_")));		
		resp.setContentLength((int) rf.length());
		try {
			input = new FileInputStream(rf);
			buf = new BufferedInputStream(input);
			stream = resp.getOutputStream();
			int readBytes = 0;
			while((readBytes = buf.read()) != -1)
				stream.write(readBytes);
		} finally {
			if(input != null) try { input.close(); } catch(IOException e) {e.printStackTrace();}
			if(stream != null) try { stream.close(); } catch(IOException e) {e.printStackTrace();}
			if(buf != null) try { buf.close(); } catch(IOException e) {e.printStackTrace();}
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
