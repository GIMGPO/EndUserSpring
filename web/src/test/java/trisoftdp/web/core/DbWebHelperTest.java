package trisoftdp.web.core;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import org.junit.Test;

public class DbWebHelperTest {

	@Test
	public void test() {
		try {
			DbWebHelper.getResultId("md5");
		} catch (ServletException e) {
			e.printStackTrace();
			fail(e.getMessage());			
		}

	}

}
