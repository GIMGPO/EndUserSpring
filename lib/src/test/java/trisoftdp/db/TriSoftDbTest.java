package trisoftdp.db;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.junit.Test;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.DynamicPublishingPackage.DitaMap;
import trisoftdp.core.DynamicPublishingPackage.Profile;
import trisoftdp.core.DynamicPublishingPackage.ProfileValue;
import trisoftdp.core.ToolKit;

public class TriSoftDbTest {
	private static final String dbURL;
	private static final String dbClass;
	private static final String sampleFile;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("unittest");
		dbURL = bundle.getString("dbURL");
		dbClass = bundle.getString("dbClass");
		sampleFile = bundle.getString("sampleFile");		
	}

	private static final TriSoftDb initDB() { 
		TriSoftDb db = null;
		try {
			Class<?> clazz =  Class.forName(dbClass);
			db = (TriSoftDb) clazz.getConstructor(String.class).newInstance(dbURL);
		} catch (IllegalArgumentException e) {			
			e.printStackTrace();
		} catch (SecurityException e) {			
			e.printStackTrace();
		} catch (InstantiationException e) {			
			e.printStackTrace();
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		} catch (InvocationTargetException e) {			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
		return db;
	}

	//	@Test
	public void miscTest() {
		String mark = "mark4";
		String failedNode = "failed";
		String pubResults = "pub_results";
		TriSoftDb db = null;
		DynamicPublishingPackage request = new DynamicPublishingPackage();
		request.packageName = "UNIT_TEST";
		request.ditaMaps = new DitaMap[]{new DitaMap()};
		request.ditaMaps[0].file = "bookmapFile";
		request.ditaMaps[0].task = "bookmapTask";
		request.ditaMaps[0].title = "bookmapTitle";
		request.languages = new String[] {"EN-US"}; 
		request.outputType = DynamicPublishingPackage.OUTPUT_TYPE.pdf2;
		request.profiles = new Profile[]{new Profile()};
		request.profiles[0].id = "1";
		request.profiles[0].name = "storage_system";
		request.profiles[0].values = new ProfileValue[]{new ProfileValue()};
		request.profiles[0].values[0].name="VNX5100";
		request.pubPackage = "generic";
		request.quesFirst = DynamicPublishingPackage.REVERSE.no;
		File tmpResult = null;
		try {
			String md5 =ToolKit.getMD5(request);
			db = initDB();			
			long resultId = db.getResultId(md5);
			if(resultId >= 0) {
				db.markRecord(resultId, mark);
				db.addFailedJob(failedNode, md5, request);
				db.getResultToStream(resultId, null);
				long[] resultsId = db.getAllResultIds();
				assertTrue("db.getAllResultIds() == 0", resultsId.length != 0);
				long[] markedResults = db.getMarkedResultIds(mark);
				assertTrue("db.getMarkedResultIds(mark) == 0", markedResults.length != 0);
				assertTrue("db.deletePubResultsEntry(resultId) == -1", db.deletePubResultsEntry(resultId) != -1);
			}			
			assertTrue("db.cleanPubResults(mark) == -1", db.cleanPubResults(mark) != -1);
			assertTrue("db.selectPubResults(pubResults) == -1", db.selectPubResults(pubResults) != -1);
			assertTrue("db.cleanPubResults() == -1", db.cleanPubResults() != -1);
			assertTrue("db.cleanPubResults(mark) == -1", db.cleanPubResults(mark) != -1);
		} 
		catch (SQLException e) {
			fail("SQLException: " + e.getMessage());			
		} catch (DynException e) {
			fail("DynException: " + e.getMessage());			
		} finally {
			if(db != null) try { db.close();} catch(Exception e) {}
			if(tmpResult != null) tmpResult.delete();
		}
	}

//	@Test
	public void writeReadNullResult() {
		TriSoftDb db = null;
		long resultId = ToolKit.generateId();
		DynamicPublishingPackage request = new DynamicPublishingPackage();
		request.packageName = "UNIT_TEST";
		request.ditaMaps = new DitaMap[]{new DitaMap()};
		request.ditaMaps[0].file = "bookmapFile";
		request.ditaMaps[0].task = "bookmapTask";
		request.ditaMaps[0].title = "bookmapTitle";
		request.languages = new String[] {"EN-US"}; 
		request.outputType = DynamicPublishingPackage.OUTPUT_TYPE.pdf2;
		request.profiles = new Profile[]{new Profile()};
		request.profiles[0].id = "1";
		request.profiles[0].name = "storage_system";
		request.profiles[0].values = new ProfileValue[]{new ProfileValue()};
		request.profiles[0].values[0].name="VNX5100";
		request.pubPackage = "generic";
		request.quesFirst = DynamicPublishingPackage.REVERSE.no;

		try {
			String md5 =ToolKit.getMD5(request);
			db = initDB();
			long id = db.getResultId(md5);
			if(id > 0) {
				System.out.println("deleting record with id=" + id);
				db.deletePubResultsEntry(id);				
			}
			db.saveResult(resultId, md5, request, null);
			db.getResultToStream(resultId, System.out);
		} 
		catch (SQLException e) {
			fail("SQLException: " + e.getMessage());			
		} catch (DynException e) {
			fail("DynException: " + e.getMessage());			
		} finally {
			if(db != null) try { db.close();} catch(Exception e) {}
		}
	}

	//	@Test
	public void getRequestById() {
		TriSoftDb db = null;
		try {
			long id = 1354483168967L;
			System.out.format("getting request for id=%d\n", id);
			db = initDB();
			Serializable request = db.getRequest(id);
			assertTrue("request == null", request != null);
			assertTrue("request is not instanceof DynamicPublishingPackage", request instanceof DynamicPublishingPackage);
		} 
		catch (SQLException e) {
			fail("SQLException: " + e.getMessage());			
		} finally {
			if(db != null) try { db.close();} catch(Exception e) {}
		}
	}

	//	@Test
	public void getResId() {
		TriSoftDb db = null;
		try {
			String md5 = "b013761102474f0fb3d97fd2ca9a45ff";
			db = initDB();
			long id = db.getResultId(md5);
			System.out.format("got id=%d\n", id);
			assertTrue("id > 0", id <= 0);
		} 
		catch (SQLException e) {
			fail("SQLException: " + e.getMessage());			
		} finally {
			if(db != null) try { db.close();} catch(Exception e) {}
		}
	}

	//	@Test
	public void saveAndReadTest() {
		TriSoftDb db = null;
		long resultId = ToolKit.generateId();
		DynamicPublishingPackage request = new DynamicPublishingPackage();
		request.packageName = "UNIT_TEST";
		request.ditaMaps = new DitaMap[]{new DitaMap()};
		request.ditaMaps[0].file = "bookmapFile";
		request.ditaMaps[0].task = "bookmapTask";
		request.ditaMaps[0].title = "bookmapTitle";
		request.languages = new String[] {"EN-US"}; 
		request.outputType = DynamicPublishingPackage.OUTPUT_TYPE.pdf2;
		request.profiles = new Profile[]{new Profile()};
		request.profiles[0].id = "1";
		request.profiles[0].name = "storage_system";
		request.profiles[0].values = new ProfileValue[]{new ProfileValue()};
		request.profiles[0].values[0].name="VNX5100";
		request.pubPackage = "generic";
		request.quesFirst = DynamicPublishingPackage.REVERSE.no;
		File result = new File(sampleFile); //any file from the local file system
		File tmpResult = null;
		try {
			tmpResult = File.createTempFile("unittest", "result");
			tmpResult.deleteOnExit();
			String md5 =ToolKit.getMD5(request);
			db = initDB();
			//check if md5 is already in the DB
			long id = db.getResultId(md5);
			if(id > 0) {
				System.out.println("deleting record with id=" + id);
				db.deletePubResultsEntry(id);				
			}
			// can save the result
			db.saveResult(resultId, md5, request, result);
			// can read the record
			Serializable r = db.getRequest(resultId);
			System.out.println("r.getClass().getName()=" + r.getClass().getName());
			assertTrue("r is not instanceof DynamicPublishingPackage", r instanceof DynamicPublishingPackage);
			assertTrue("db.getResultId(md5) != resultId", db.getResultId(md5) == resultId);
			db.getResultToFile(resultId, tmpResult);
			assertTrue("tmpResult.length() != result.length()", tmpResult.length() == result.length());
		} 
		catch (SQLException e) {
			fail("SQLException: " + e.getMessage());			
		} catch (DynException e) {
			fail("DynException: " + e.getMessage());			
		} catch (IOException e) {
			fail("IOException: " + e.getMessage());			
		} finally {
			if(db != null) try { db.close();} catch(Exception e) {}
			if(tmpResult != null) tmpResult.delete();
		}
	}

}
