package trisoftdp.web.db;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;

/**
 * Implementations of this interface should have constructors:
 * <pre>
 * 		public SomeNameDB(String url) throws SQLException {
 * 			......
 * 		}
 *	 	public SomeNameDB() throws SQLException {
 *			......
 *		}
 * </pre> 
 * The first constructor is for application and test context. 
 * The second is for web context, where connection is taken from a pool of connections.
 * @author shadrn1
 *
 */
public interface TriSoftDb {

	/**
	 * Gets the list of all document ids 
	 * 
	 * @return all saved result ids
	 * @throws SQLException 
	 */
	public long[] getAllResultIds () throws SQLException;

	/**
	 * Gets the list of all document ids that were marked as "mark"
	 * 
	 * @param mark record marker
	 * @return all result ids marked as "mark"
	 * @throws SQLException 
	 */
	public long[] getMarkedResultIds (String mark) throws SQLException;

	/**
	 * Insert the record into the pub_results table:
	 * 
	 * cdate: current date;
	 * 
	 * @param resultId - goes to the result_id field
	 * @md5 md5 - MessageDigest of the serialized request, goes to md5 field
	 * @param request - goes to request_obj field
	 * @param result - goes to result_data field. If result==null, then there should be a record with null in this field.
	 * @throws SQLException
	 */
	public void saveResult(long resultId, String md5, Serializable request, File result) throws SQLException;

	/**
	 * Insert the record into the failed_jobs table:  
	 * 
	 * cdate field: current date;
	 * 
	 * @param md5 - MessageDigest of the serialized request, goes to md5 field
	 * @param note - goes to note field
	 * @param request - goes to request_obj field
	 * @throws SQLException
	 */
	public void addFailedJob(String note, String md5, Serializable request) throws SQLException;

	/**
	 * Insert the record into the marked_records table:
	 *  
	 * @param resultId - goes to the result_id field
	 * @param mark - goes to the mark field
	 * @throws SQLException
	 */
	public void markRecord(long resultId, String mark) throws SQLException;

	/**
	 * Select result_id from the pub_results table where md5=md5
	 * 
	 * @param md5
	 * @return result_id
	 * @throws SQLException
	 */
	public long getResultId(String md5) throws SQLException;

	/**
	 * Select request_obj from the pub_results table where resultId=resultId
	 * 
	 * @param resultId
	 * @return request_obj
	 * @throws SQLException
	 */
	public Serializable getRequest(long resultId) throws SQLException; 

	/**
	 * Select result_data from pub_results table where result_id=resultId and
	 * put the data selected in the file.
	 * 
	 * @param resultId
	 * @param result
	 * @throws SQLException
	 */
	public void getResultToFile(long resultId, File result) throws SQLException;

	/**
	 * Select result_data from pub_results table where result_id=resultId and
	 * put the data selected in the output stream.
	 * 
	 * @param resultId
	 * @param out
	 * @throws SQLException
	 */
	public void getResultToStream(long resultId, OutputStream out) throws SQLException;

	/**
	 * Closes the connection to the database if it is not null.
	 * 
	 * @throws SQLException
	 */
	//public void close() throws SQLException ;

	/**
	 * If the connection to the database is not null, sets AutoCommit to false.
	 * 
	 * @throws SQLException
	 */
	//public void startTransaction() throws SQLException ;

	/**
	 * 
	 * @throws SQLException
	 */
	//public void rollback() throws SQLException;

	/**
	 * 
	 * @throws SQLException
	 */
	//public void commitTransaction () throws SQLException ;


	/* For the admin part */

	/**
	 * Select the number of records from tableName
	 * 
	 * @param tableName
	 * @throws SQLException
	 */
	public int selectPubResults(String tableName) throws SQLException ;

	/**
	 * Delete all records from the pub_results table 
	 * where (result_id, exemptMark) pair is not in marked_records table
	 * 
	 * @param exemptMark
	 * @return number of erased records
	 * @throws SQLException
	 */
	public int cleanPubResults(String exemptMark) throws SQLException ;

	/**
	 * Delete all records from the pub_results table 
	 * (on delete cascade should delete the marked_records automatically)
	 * 
	 * @return number of erased records 
	 * @throws SQLException
	 */
	public int cleanPubResults() throws SQLException;

	/**
	 * Delete the record from the pub_result table where result_id=resId
	 * (if the record is marked, it should be deleted automatically 
	 * from the marked_records table)
	 * 
	 * @param resId  
	 * @return number of erased records (should be 1)
	 * @throws SQLException
	 */
	public int deletePubResultsEntry(long resId) throws SQLException ;

}
