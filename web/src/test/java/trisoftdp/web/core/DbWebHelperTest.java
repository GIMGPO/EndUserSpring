package trisoftdp.web.core;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import org.junit.Test;

public class DbWebHelperTest {

	@Test
	public void fakeTest() {}
	
	@Test
	public void test() {
		try {
			long id = DbWebHelper.getResultId("md5");
			System.out.format("id=%d%n", id);
		} catch (ServletException e) {
			e.printStackTrace();
			fail(e.getMessage());			
		}

	}

}
