package servlets;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.oreilly.servlet.MultipartRequest;

/**
 * Wrapper for a file upload request.
 * 
 */
public class FileUploadWrapper extends HttpServletRequestWrapper {

	MultipartRequest mreq = null;
	public FileUploadWrapper(HttpServletRequest req, String dir)
	throws IOException {
		super(req);
		mreq = new MultipartRequest(req, dir, 75*1024*1024);
	}
	// Methods to replace HSR methods
	@Override
	public Enumeration getParameterNames() {
		return mreq.getParameterNames();
	}
	@Override
	public String getParameter(String name) {
		return mreq.getParameter(name);
	}
	@Override
	public String[] getParameterValues(String name) {
		return mreq.getParameterValues(name);
	}
	@Override
	public Map getParameterMap() {
		Map map = new HashMap();
		Enumeration en = getParameterNames();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			map.put(name, mreq.getParameterValues(name));
		}
		return map;
	}
	// Methods only in MultipartRequest
	public Enumeration getFileNames() {
		return mreq.getFileNames();
	}
	public String getFilesystemName(String name) {
		return mreq.getFilesystemName(name);
	}
	public String getContentType(String name) {
		return mreq.getContentType(name);
	}
	public File getFile(String name) {
		return mreq.getFile(name);
	}

} 
