package trisoftdp.core;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class StringsTest {

	@Test
	public void ReplacePatternTest() {
		String str = "Related documentation: VNX for Bl\\ock OE 5/33 and VNX ____for File OE 8.1";
//		str = str.replaceAll("\\s+", "_").replace(":", "_").replace(".", "_");
//		str = str.replace("\\s+", "_");
//		str = str.replaceAll("[\\s+,:,;,/,\\\\,\\.]", "_").replaceAll("__*","_");
		str = str.replaceAll("[^a-z,A-Z,0-9]", "_").replaceAll("__*","_");
		//str = str.replaceAll("__*","_");
		System.out.println(str);
	}
	
	@Test
	public void GetFolderTest() {
		String fullPath= "Y/:\\EMC_LCA_DOCS\\VNX\\";
		File f = new File(fullPath);
		String folder = f.getName();
		System.out.println(folder);
	}

}
