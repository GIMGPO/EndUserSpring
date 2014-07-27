package trisoftdp.db;

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

public class TrisoftDbImpl implements TriSoftDb {

	private Connection conn;
	public static String url = "jdbc:sqlserver://edpappprd10:1433;databaseName=TrisoftDP;userName=sa;password=TechPub2005;";
	public static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public TrisoftDbImpl() throws SQLException 
	{		
		try {
			InitialContext ic = new InitialContext();
			Context envContext  = (Context)ic.lookup("java:/comp/env");
			if(envContext == null)
				throw new SQLException("Failed to lookup java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/TrisoftDB");
			if(ds == null) 
				throw new SQLException("Failed to lookup data source");
			conn =  ds.getConnection();
			if(conn == null) 
				throw new SQLException("Failed to get connection to DB");
		} catch (NamingException e) {
			throw new SQLException("NamingException: " + e.getMessage());
		}
	}
	public TrisoftDbImpl(String url) {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to load Driver Class " + driver);
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
	}

//	@Override
	public void saveResult(long resultId, String md5, Serializable request,	File result) throws SQLException 
	{
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
			if(fis != null) try {fis.close();} catch(Exception e) {}
			if(oos != null) try { oos.close(); } catch (Exception e) {} //relax
			if(baos != null) try { baos.close(); } catch (Exception e) {} //relax
			if(bais != null) try { bais.close(); } catch (Exception e) {} //relax
			if(saveResult != null) try { saveResult.close();} catch (Exception e) {} //relax
		}
	}

//	@Override
	public void addFailedJob(String note, String md5, Serializable request)	throws SQLException 
	{
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		byte[] obj = null;
		PreparedStatement addFailedJob = null;            
		try {
			addFailedJob = conn.prepareStatement("INSERT INTO failed_jobs(md5,request_obj,note) VALUES (?, ?, ?)");
			addFailedJob.setString(1, md5);			
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(request);
			obj = baos.toByteArray();
			bais = new ByteArrayInputStream(obj);
			addFailedJob.setBinaryStream(2, bais, obj.length);
			addFailedJob.setString(3, note);
			addFailedJob.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new SQLException("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			throw new SQLException("IOException: " + e.getMessage());
		} finally {
			if(fis != null) try {fis.close();} catch(Exception e) {}
			if(oos != null) try { oos.close(); } catch (Exception e) {} //relax
			if(baos != null) try { baos.close(); } catch (Exception e) {} //relax
			if(bais != null) try { bais.close(); } catch (Exception e) {} //relax
			if(addFailedJob != null) try { addFailedJob.close();} catch (Exception e) {} //relax
		}
	}

//	@Override
	public void markRecord(long resultId, String mark) throws SQLException 
	{
		PreparedStatement markRecord = null;            
		try {
			markRecord = conn.prepareStatement("INSERT INTO marked_records(result_id,mark) VALUES (?, ?)");
			markRecord.setLong(1, resultId);
			markRecord.setString(2, mark);			
			markRecord.executeUpdate();
		} catch (Exception e) {
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(markRecord != null) try { markRecord.close();} catch (Exception e) {} //relax
		}
	}

//	@Override
	public long getResultId(String md5) throws SQLException 
	{
		Statement statement = null;
		ResultSet result = null;
		long resultId = 0;
		try {
			statement = conn.createStatement();
			String query = "SELECT result_id FROM pub_results WHERE md5 = '" + md5 + "'";  
			result = statement.executeQuery(query);  
			while (result.next()) { 
				resultId = result.getLong("result_id");
			}
		}
		catch(SQLException se) {
			se.printStackTrace(); 
		} finally {
			if(result != null ) try {result.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
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
			se.printStackTrace(); } 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(result != null ) try {result.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return request;
	}

//	@Override
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
				is.close();
				out.flush();
				out.close();
			}
		} catch(SQLException se) {
			se.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(is != null ) try {is.close();} catch(IOException e) {e.printStackTrace();}
			if(out != null ) try {out.close();} catch(IOException e) {e.printStackTrace();}
			if(rs != null ) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}		
		}
	}

//	@Override
	public void getResultToStream(long resultId, OutputStream out)	throws SQLException 
	{
		Statement statement = null;
		ResultSet rs = null;
		InputStream is = null;
		String query = "SELECT result_data FROM pub_results WHERE result_id = " + resultId;  		
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(query);  
			while (rs.next()) { 
				is = rs.getBinaryStream("result_data");
				int nextChar;
				if(is != null) {
					while ((nextChar = is.read())!= -1)
						out.write((char)nextChar);
					is.close();	
				}				
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(is != null ) try {is.close();} catch(IOException e) {e.printStackTrace();}
			if(rs != null ) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}			
		}
	}

//	@Override
	public void close() throws SQLException {
		if(conn != null)
			conn.close();
	}

//	@Override
	public void startTransaction() throws SQLException {
		if(conn != null)
			conn.setAutoCommit(false);
	}

//	@Override
	public void rollback() throws SQLException {
		if(conn != null)
			conn.rollback();
	}

//	@Override
	public void commitTransaction() throws SQLException {
		if(conn != null)
			conn.commit();
	}

//	@Override
	public int selectPubResults(String tableName) throws SQLException {
		Statement statement = null;
		ResultSet result = null;
		int noOfRows = 0;
		try {
			statement = conn.createStatement();
			String query = "SELECT count(*) as rows FROM " + tableName;  
			result = statement.executeQuery(query);  
			while (result.next()) 
				noOfRows = result.getInt("rows");
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(result != null ) try {result.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}	
		}
		return noOfRows;
	}

//	@Override
	public int cleanPubResults(String exemptMark) throws SQLException 
	{
		Statement stmt1 = null;
		int deletedCount = 0;
		try {
			stmt1 = conn.createStatement();
			String query1 = "DELETE FROM pub_results WHERE result_id NOT IN (SELECT result_id FROM marked_records WHERE mark = '" + exemptMark + "')";  
			deletedCount = stmt1.executeUpdate(query1);  		
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(stmt1 != null) try {stmt1.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return deletedCount;
	}

//	@Override
	public int cleanPubResults() throws SQLException 
	{
		Statement stmt2 = null;
		int deletedCount = 0;
		try {
			stmt2 = conn.createStatement();
			String query2 = "DELETE FROM pub_results";  
			deletedCount = deletedCount + stmt2.executeUpdate(query2);
		} catch(SQLException se) {
			se.printStackTrace(); 
		} finally {
			if(stmt2 != null) try{ stmt2.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return deletedCount;
	}

//	@Override
	public int deletePubResultsEntry(long resId) throws SQLException 
	{
		Statement stmt1 = null;
		int deletedCount = 0;
		try {
			stmt1 = conn.createStatement();
			String query1 = "DELETE FROM pub_results WHERE result_id = " + resId;  
			deletedCount = stmt1.executeUpdate(query1);  	
		}
		catch(SQLException se) {
			se.printStackTrace(); }
		finally {
			if(stmt1 != null) try { stmt1.close(); } catch(SQLException e) {e.printStackTrace();}
		}
		return deletedCount;
	}

//	@Override
	public long[] getAllResultIds() {
		Statement statement = null;
		ResultSet rs = null;
		List<Long> results = new ArrayList<Long>();
		try {
			statement = conn.createStatement();
			String query = "SELECT DISTINCT result_id FROM pub_results";  
			rs = statement.executeQuery(query);  
			while (rs.next())
				results.add(rs.getLong("result_id"));
		} catch(SQLException se) {
			se.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
		}
		long[] resultIds = new long[results.size()];
		for(int i=0;i<results.size();i++)
			resultIds[i] = results.get(i);
		return resultIds;
	}


//	@Override
	public long[] getMarkedResultIds(String mark) {
		Statement statement = null;
		ResultSet rs = null;
		long[] resultIds = null;
		List<Long> results = new ArrayList<Long>();
		try {
			statement = conn.createStatement();
			String query = "SELECT DISTINCT result_id FROM marked_records WHERE mark = '" + mark + "'";  
			rs = statement.executeQuery(query); 			
			while (rs.next())				
				results.add(rs.getLong("result_id"));
			resultIds = new long[results.size()];
			for(int i=0;i<results.size();i++)
				resultIds[i] = results.get(i);
		}
		catch(SQLException se) {
			se.printStackTrace(); } 
		finally {
			if(rs != null ) try {rs.close();} catch(SQLException e) {e.printStackTrace();}
			if(statement != null ) try {statement.close();} catch(SQLException e) {e.printStackTrace();}
		}
		return resultIds;
	}

}
