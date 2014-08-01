package trisoftdp.web.core;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import org.junit.Test;

import trisoftdp.core.ToolKit;

public class DbWebHelperTest {

	@Test
	public void test() {
		try {
			long resId = DbWebHelper.getResultId("md5");
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
