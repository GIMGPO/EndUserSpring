package trisoftdp.web.core;

import java.sql.SQLException;

import javax.servlet.ServletException;

import trisoftdp.web.db.TriSoftDb;
import trisoftdp.web.db.TriSoftDbHelper;


public class DbWebHelper {

	public static int cleanPubResults() throws ServletException {
		TriSoftDb db = null;	
		int result = -1;
		try {
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			result = db.cleanPubResults();
		} catch (SQLException e) {
			throw new ServletException("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {}
		}
		return result;
	}

	public static int cleanPubResults(String exemptMark) throws ServletException {
		TriSoftDb db = null;	
		int result = -1;
		try {
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			result = db.cleanPubResults(exemptMark);
		} catch (SQLException e) {
			throw new ServletException("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {}
		}
		return result;
	}

	public static int deletePubResultsEntry(long resId) throws ServletException {
		TriSoftDb db = null;	
		int result = -1;
		try {
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			result = db.deletePubResultsEntry(resId);
		} catch (SQLException e) {
			throw new ServletException("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {}
		}
		return result;
	}

	public static int selectPubResults(String tableName) throws ServletException {
		TriSoftDb db = null;	
		int result = -1;
		try {
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			result = db.selectPubResults(tableName);
		} catch (SQLException e) {
			throw new ServletException("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {}
		}
		return result;
	}

	public static long getResultId(String md5) throws ServletException {
		TriSoftDb db = null;	
		long result = -1;
		try {
			//db = ToolKit.newDB();
			db = new TriSoftDbHelper();
			result = db.getResultId(md5);
		} catch (SQLException e) {
			throw new ServletException("SQLException: " + e.getMessage());
		} finally {
			if(db != null) try { db.close(); } catch(Exception e) {}
		}
		return result;
	}

}
