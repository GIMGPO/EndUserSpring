package trisoftdp.db;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import trisoftdp.core.CoreConstants;

public class TrisoftDbImplMySQL implements TriSoftDb {

	private Connection conn;
	public static String url = "jdbc:mysql://127.0.0.1:3306/" + CoreConstants.appPropsMap.get("MYSQL_DP_NAME");
	public TrisoftDbImplMySQL(String url) throws SQLException {
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to load Driver Class " + driver);
		} 
		conn = DriverManager.getConnection(url, "tomcat", "cat");
	}

	public TrisoftDbImplMySQL() throws SQLException {
		try {
			InitialContext ic = new InitialContext();
			Context envContext  = (Context)ic.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/" + CoreConstants.appPropsMap.get("MYSQL_DP_NAME"));
			conn =  ds.getConnection();
			CoreConstants.logger.info("Connection to the database established");
		} catch (NamingException e) {
			throw new SQLException("NamingException: " + e.getMessage());
		}
	}

	public String test () throws SQLException {
		String result = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			stmt = conn.createStatement();
			String cmd = "Select * FROM test";
			rst = stmt.executeQuery(cmd);
			while (rst.next())
				result = "#" + rst.getString("id") + ": " + rst.getString("data");
		} finally {
			if(rst != null) try { rst.close(); } catch(SQLException e) {e.printStackTrace();}
			if(stmt != null) try { stmt.close(); } catch(SQLException e) {e.printStackTrace();}
		}
		return result;
	}


//	@Override
	public void saveResult (long resultId, String md5, Serializable request, File result) throws SQLException {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		byte[] obj = null;
		PreparedStatement saveResult = null;            
		try {
			if(result != null) {
				saveResult = conn.prepareStatement("INSERT INTO pub_results (result_id, md5, request_obj, result_data) VALUES (?, ?, ?, ?)");
				fis = new FileInputStream(result);  
				saveResult.setBinaryStream(4, fis, (int) result.length());
			}
			else
				saveResult = conn.prepareStatement("INSERT INTO pub_results (result_id, md5, request_obj) VALUES (?, ?, ?)");	
			saveResult.setLong(1, resultId);
			saveResult.setString(2, md5);			            
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(request);
			obj = baos.toByteArray();
			bais = new ByteArrayInputStream(obj);
			saveResult.setBinaryStream(3, bais, obj.length);
			saveResult.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new SQLException("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} finally {
			if(fis != null) try {fis.close();} catch(IOException e) {e.printStackTrace();}
			if(oos != null) try { oos.close(); } catch (IOException e) {e.printStackTrace();}
			if(baos != null) try { baos.close(); } catch (IOException e) {e.printStackTrace();}
			if(bais != null) try { bais.close(); } catch (IOException e) {e.printStackTrace();}
			if(saveResult != null) try { saveResult.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

//	@Override
	public void addFailedJob(String note, String md5, Serializable request) throws SQLException {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		byte[] obj = null;
		PreparedStatement addFailedJob = null;		
		try {
			addFailedJob = conn.prepareStatement("INSERT INTO failed_jobs (md5, request_obj, note) VALUES (?, ?, ?)");
			addFailedJob.setString(1, md5);			
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(request);
			obj = baos.toByteArray();
			bais = new ByteArrayInputStream(obj);
			addFailedJob.setBinaryStream(2, bais, obj.length);
			addFailedJob.setString(3, note);
			addFailedJob.executeUpdate();
		} catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} finally {
			if(oos != null) try { oos.close(); } catch (IOException e) {e.printStackTrace();}
			if(baos != null) try { baos.close(); } catch (IOException e) {e.printStackTrace();}
			if(bais != null) try { bais.close(); } catch (IOException e) {e.printStackTrace();}
			if(addFailedJob != null) try { addFailedJob.close();} catch (SQLException e) {e.printStackTrace();}
		}
	}
	
//	@Override
	public void markRecord(long resultId, String mark) throws SQLException {			
		PreparedStatement markRecord = null;            
		try {
			markRecord = conn.prepareStatement("INSERT INTO marked_records(result_id,mark) VALUES (?, ?)");
			markRecord.setLong(1, resultId);
			markRecord.setString(2, mark);			
			markRecord.executeUpdate();
		} catch (Exception e) {
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(markRecord != null) try { markRecord.close();} catch (SQLException e) {e.printStackTrace();}
		}
	}

//	@Override
	public long getResultId(String md5) throws SQLException {
		long resultId = -1;
		Statement stmt = null;
		ResultSet rst = null;
		stmt = conn.createStatement();
		try {
		rst = stmt.executeQuery("SELECT result_id FROM pub_results WHERE md5='" + md5 + "'");
		if(rst.next())
			resultId = rst.getLong("result_id");
		} finally {
			if(rst != null) try {rst.close();} catch(SQLException e) {e.printStackTrace();}
			if(stmt != null) try {stmt.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return resultId;
	}

//	@Override
	public Serializable getRequest(long resultId) throws SQLException 
	{
		Statement statement = null;
		ResultSet result = null;
		Serializable request = null;
		try {
			statement = conn.createStatement();
			String query = "SELECT request_obj FROM pub_results WHERE result_id = " + resultId;  
			result = statement.executeQuery(query);  
			while (result.next()) { 
				Object obj = null;
				byte[] buf = result.getBytes("request_obj");
				if (buf != null) {
					ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
					obj = objectIn.readObject();
				}
				request = (Serializable)obj;
			}
		}
		catch(SQLException se) {
			throw new SQLException("SQLException: " + se.getMessage()); 
		} catch (FileNotFoundException e) {
			throw new SQLException("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new SQLException("ClassNotFoundException: " + e.getMessage());
		} 
		finally {
			if(result != null) try {result.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return request;
	}

	//@Override
	public void getResultToFile(long resultId, File result) throws SQLException 
	{
		Statement statement = null;
		ResultSet rs = null;
		InputStream is = null;
		OutputStream out = null;
		try {
			statement = conn.createStatement();
			String query = "SELECT result_data FROM pub_results WHERE result_id = " + resultId;  
			rs = statement.executeQuery(query);
			while (rs.next()) { 
				is = rs.getBinaryStream("result_data");
				out = new FileOutputStream(result);
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = is.read(bytes)) != -1)
					out.write(bytes, 0, read);
				out.flush();
				try {is.close();} catch(IOException e) {e.printStackTrace();}
				try {out.close();} catch(IOException e) {e.printStackTrace();}
			}
		}catch(SQLException e) {
			throw new SQLException("SQLException: " + e.getMessage()); } 
		catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} finally {
			if(out != null) try {out.close();} catch(IOException e) {e.printStackTrace();}
			if(rs != null) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
		}
	}

	//@Override
	public void getResultToStream(long resultId, OutputStream out) throws SQLException {
		BufferedInputStream bis = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery("SELECT result_data FROM pub_results WHERE result_id=" + resultId);
			if(rst.next()) {
				int c;
				bis = new BufferedInputStream(rst.getBinaryStream("result_data"));			
				while( (c = bis.read()) != -1)
					out.write((byte) c);
			}
		} catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} finally {
			if(rst != null) try {rst.close();} catch(SQLException e) {e.printStackTrace();}
			if(stmt != null) try {stmt.close();} catch(SQLException e) {e.printStackTrace();}
		}
	}

	//@Override
	public void close() throws SQLException {
		if (conn != null)
			conn.close();
	}

	//@Override
	public void startTransaction() throws SQLException {
		if (conn != null)
			conn.setAutoCommit(false);
	}

	//@Override
	public void rollback() throws SQLException {
		if (conn != null)
			conn.rollback();
	}

	//@Override
	public void commitTransaction () throws SQLException {
		if (conn != null)
			conn.commit();
	}


	/* For the admin part */

	//@Override
	public int selectPubResults(String tableName) throws SQLException {			
		Statement stmt = null;
		ResultSet rs = null;
		int rowCount = -1;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
			// get the number of rows from the result set
			rs.next();
			rowCount = rs.getInt(1);
		} finally {
			if(rs != null) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(stmt != null) try {stmt.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return rowCount;
	}

	//@Override
	public int cleanPubResults(String exemptMark) throws SQLException {			
		int rows = -1;
		if(conn != null) {
			PreparedStatement pstmt = null;
			try {
				String query = "DELETE FROM pub_results WHERE NOT result_id IN (SELECT result_id FROM marked_records WHERE mark=?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, exemptMark);
				rows = pstmt.executeUpdate();
				if(rows > 0) {
					CoreConstants.logger.info("cleanPubResults(\"" + exemptMark + "\"): " + rows + " records deleted.");
				} 
			} catch(SQLException sqle) {
				sqle.printStackTrace();
			} finally {
				if(pstmt != null) try {pstmt.close();} catch (SQLException sqle) { sqle.printStackTrace(); }
			}
		}
		return rows;
	}


	//@Override
	public int cleanPubResults() throws SQLException {	
		int rows = -1;
		if(conn != null) {
			PreparedStatement pstmt = null;
			try {
				String query = "DELETE FROM pub_results";
				pstmt = conn.prepareStatement(query);
				rows = pstmt.executeUpdate();
				if(rows > 0) {
					CoreConstants.logger.info("cleanPubResults(): " + rows + " records deleted.");
				} 
			} catch(SQLException sqle) {
				sqle.printStackTrace();
			} finally {
				if(pstmt !=null) try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			}
		}
		return rows;
	}

	//@Override
	public int deletePubResultsEntry(long resId) throws SQLException {	
		Statement stmt1 = null;
		int deletedCount = 0;
		try {
			stmt1 = conn.createStatement();
			String query1 = "DELETE FROM pub_results WHERE result_id = " + resId;  
			deletedCount = stmt1.executeUpdate(query1);  	
		}
		catch(SQLException se) {
			throw new SQLException("SQLException: " + se.getMessage()); }
		finally {
			if(stmt1 !=null) try {stmt1.close();} catch (SQLException e) {e.printStackTrace();}
		}
		return deletedCount;
	}

	//@Override
	public long[] getAllResultIds() {
		Statement statement = null;
		ResultSet rs = null;
		List<Long> results = new ArrayList<Long>();
		try {
			statement = conn.createStatement();
			String query = "SELECT result_id FROM pub_results";  
			rs = statement.executeQuery(query);  
			while (rs.next())				
				results.add(rs.getLong("result_id"));
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(rs !=null) try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(statement !=null) try {statement.close();} catch (SQLException e) {e.printStackTrace();}
		}
		long[] resultIds = new long[results.size()];
		for(int i=0;i<results.size();i++)
			resultIds[i] = results.get(i);
		return resultIds;
	}

	//@Override
	public long[] getMarkedResultIds(String mark) {
		Statement statement = null;
		ResultSet rs = null;
		List<Long> results = new ArrayList<Long>();
		try {
			statement = conn.createStatement();
			String query = "SELECT result_id FROM marked_records WHERE mark = '" + mark + "'";  
			rs = statement.executeQuery(query); 			
			while (rs.next())				
				results.add(rs.getLong("result_id"));			
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(rs !=null) try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(statement !=null) try {statement.close();} catch (SQLException e) {e.printStackTrace();}							
		}
		long[] resultIds = new long[results.size()];
		for(int i=0;i<results.size();i++)
			resultIds[i] = results.get(i);
		return resultIds;
	}

}

