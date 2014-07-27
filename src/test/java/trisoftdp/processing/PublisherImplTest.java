package trisoftdp.processing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import trisoftdp.core.DynException;
import trisoftdp.core.ToolKit;

public class PublisherImplTest {

	@Test
	public void fakeTest() {} 
//	@Test	
	public void targetDirTest() {
		long id = ToolKit.generateId();
		Publisher p = new PublisherImpl();
		File targetDir = p.targetDir(id, "generic");
		System.out.println("targetDir:" + targetDir);
	}

//	@Test	
	public void runAntTest() {
		File targetDir = new File("C:\\EMC_TRISOFT_DOCS\\VNX\\work\\ghe_p_vnx_processor_enclosure_tech_specs_1234567890");
		//		long id = ToolKit.generateId();
		long id = 1234567890;
		Publisher p = new PublisherImpl();

		try {
			p.runAnt(targetDir,id);
			//			assertTrue("deleted2 == -1", deleted2 != -1);
			File outputDir = new File(targetDir,"output");
			assertTrue("output dir does not exist", outputDir.exists());
			boolean pdfExists = false;
			for(String f: outputDir.list())
				if(f.endsWith(".pdf")) {
					pdfExists = true;
					break;
				}
			assertTrue("pdf file was not found in the output dir", pdfExists);
		} catch (DynException e) {
			fail("DynException: " + e.getMessage());			
		} 
	}
}
