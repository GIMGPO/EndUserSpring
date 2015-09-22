package trisoft.web.db;

import static org.junit.Assert.*;
import org.junit.Test;
import java.sql.SQLException;

import trisoftdp.web.db.TriSoftDb;
import trisoftdp.web.db.TriSoftDbHelper;

public class TriSoftDbHelperTest {

	static TriSoftDb db = new TriSoftDbHelper();
	
//	@Test
	public void testGetAllResultIds() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetMarkedResultIds() {
		fail("Not yet implemented");
	}

//	@Test
	public void testSaveResult() {
		fail("Not yet implemented");
	}

//	@Test
	public void testAddFailedJob() {
		fail("Not yet implemented");
	}

//	@Test
	public void testMarkRecord() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetResultId() {
		String md5 = "461fd489d83c50c4c016290da8c2a531";
		try {
			long resultId = db.getResultId(md5);
			System.out.format("resultId=%d%n", resultId);
		} catch (SQLException e) {
			e.printStackTrace();			
			fail("SQLException: " + e.getMessage());
		}
	}

	
/*
 	@Test
	public void testGetJobResult() {
		long resultId = 1393350642889L;
		try {
			JobResult jr = db.getJobResult(resultId);
			assertTrue("jr == null", jr != null);
			System.out.format("resultId=%d md5=%s note=<%s> status=%s%n", resultId, jr.getMd5(), jr.getNote(), jr.getStatus());
			for(JobMark mark: jr.getMarks())
				System.out.format("id=%d mark=%s%n",mark.getId(), mark.getMark());
		} catch (SQLException e) {
			e.printStackTrace();			
			fail("SQLException: " + e.getMessage());
		}
	}
*/
	
//	@Test
	public void testGetRequest() {
		fail("Not yet implemented");
	}

/*
	@Test
	public void testSaveGetJobMark() {
		try {
			long resultId = 1393350642889L;
			JobResult jobResult = db.getJobResult(resultId);
			JobMark jm = new JobMark(jobResult, "good");
			System.out.format("1 id=%d mark=%s%n",jm.getId(), jm.getMark());
			db.saveJobMark(jm);
			System.out.format("2 id=%d mark=%s%n",jm.getId(), jm.getMark());			
			long id = jm.getId();
			JobMark jm1 = db.getJobMark(id);
			System.out.format("3 id=%d mark=%s%n",jm1.getId(), jm1.getMark());			
			assertTrue("jm == null", jm1 != null);
			jm1.setMark("bad");
			db.saveJobMark(jm1);
			JobMark jm2 = db.getJobMark(id);
			assertTrue("jm == null", jm2 != null);
			System.out.format("mark=%s%n", jm2.getMark());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException: " + e.getMessage());
		}
	}
*/

}
