package trisoftdp.maintenance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTool {

	private Connection conProd, conTest;
	private static String prodDbUrl = "jdbc:mysql://10.250.143.11:3306/esa_dyn_pub?noDatetimeStringSync=true&blobSendChunkSize=1024000000&useServerPrepStmts=true&emulateUnsupportedPstmts=false&maxAllowedPacket=1024000000";
	//private static String prodDbUrl = "jdbc:mysql://localhost:3306/dyn_pub";
	//private static String testDbUrl = "jdbc:mysql://10.106.101.161:3306/dyn_pub";
	private static String testDbUrl = "jdbc:mysql://localhost:3306/dyn_pub";

	public DbTool() {
		try {
			conProd = getConnection(prodDbUrl);
			conTest = getConnection(testDbUrl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (conProd != null)
				conProd.close();
			if (conTest != null)
				conTest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) {
		DbTool tool = new DbTool();
		try {
			tool.moveMarkedRecords();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tool.close();
	}

	private static Connection getConnection(String url) throws SQLException {
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to load Driver Class " + driver);
		} 
		Connection conn = DriverManager.getConnection(url, "tomcat", "cat");
		return conn;
	}

	public void moveMarkedRecords() throws SQLException {
		String mark = "generic";
		Statement stmtTest = null;
		PreparedStatement insertMarkedRecord = null;
		PreparedStatement insertPubResults = null;
		PreparedStatement getByResultId = null;
		PreparedStatement deletePubResults = null;
		ResultSet rst = null;
		ResultSet record = null;
		try {
			stmtTest = conTest.createStatement();
			insertMarkedRecord = conProd.prepareStatement("INSERT INTO marked_records (result_id, mark) VALUES (?, ?)");
			insertPubResults = conProd.prepareStatement("INSERT INTO pub_results (result_id, md5, cdate, request_obj, result_data) VALUES (?,?,?,?,?)");
			getByResultId = conTest.prepareStatement("SELECT * FROM pub_results WHERE result_id=?");
			deletePubResults = conProd.prepareStatement("DELETE FROM pub_results WHERE result_id=?");
			rst = stmtTest.executeQuery("SELECT pr.result_id FROM pub_results pr INNER JOIN marked_records mr ON pr.result_id = mr.result_id AND mr.mark = '"+ mark + "'");
			int count = 0;
			insertMarkedRecord.setString(2, mark);
			while (rst.next()) {
				System.out.println("result_id=" + rst.getLong("result_id")	+ "  " + ++count);
				deletePubResults.setLong(1, rst.getLong("result_id"));
				deletePubResults.executeUpdate();
				getByResultId.setLong(1, rst.getLong("result_id"));
				record = getByResultId.executeQuery();
				if (record.next()) {
					insertPubResults.setLong(1, record.getLong("result_id"));
					insertPubResults.setString(2, record.getString("md5"));
					insertPubResults.setTimestamp(3,record.getTimestamp("cdate"));
					insertPubResults.setBinaryStream(4,record.getBinaryStream("request_obj"));
					insertPubResults.setBinaryStream(5,	record.getBinaryStream("result_data"));
					insertPubResults.executeUpdate();
					insertMarkedRecord.setLong(1, record.getLong("result_id"));
					insertMarkedRecord.executeUpdate();
				}
			}
		} finally {
			if(getByResultId != null) try { getByResultId.close(); } catch(SQLException e) {e.printStackTrace();} 
			if(insertMarkedRecord != null) try { insertMarkedRecord.close(); } catch(SQLException e) {e.printStackTrace();} 
			if(insertPubResults != null) try { insertPubResults.close(); } catch(SQLException e) {e.printStackTrace();} 
			if(deletePubResults != null) try { deletePubResults.close(); } catch(SQLException e) {e.printStackTrace();} 
			if(record != null) try { record.close(); } catch(SQLException e) {e.printStackTrace();} 
			if(rst != null) try { rst.close(); } catch(SQLException e) {e.printStackTrace();}
			if(stmtTest != null) try { stmtTest.close(); } catch(SQLException e) {e.printStackTrace();} 
		}
	}

}
