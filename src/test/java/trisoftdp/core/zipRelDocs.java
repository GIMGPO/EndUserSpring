package trisoftdp.core;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.junit.Test;

public class zipRelDocs {

	@Test
	public void testZipFiles() throws DynException {
		String lang = "en_US";
		String prod = "DLM_DEV";
		ProdEnvBean prodEnv = new ProdEnvBean();
		prodEnv = new ProdEnvBean(prod, lang);
		System.out.println("New language: " + lang);
		String fileName = "";
		String relDocsXmlFileName = "";
			fileName = "all";
			String fileFullName = prodEnv.getProdRelDocsDir() + fileName;
			relDocsXmlFileName = "RelatedDocs_351";
			if ("all".equals(fileName)) {
				fileName = "DLm_3_5_1_Documentation_for_Service_only_en_US.zip";
				fileFullName = prodEnv.getProdRelDocsCacheDir() + fileName;
				File f = new File(fileFullName);
				if (f.exists()) {
					System.out.println("Zip file already exists. Downloading it.");
				} else {
					System.out.println("Zip file doesn't exist. Creating it and then downloading.");
					DownloadAllRelDocs d = new DownloadAllRelDocs();
					String relDocsXmlPath = prodEnv.getProdConfigDir() + relDocsXmlFileName + ".xml";
					List<String> filesToDownload = d.parseRelatedDocsXml(relDocsXmlPath);
					System.out.println("# of files to be zipped: " + filesToDownload.size());
					d.zipFiles(filesToDownload, fileName, prodEnv.getProdRelDocsCacheDir(), prodEnv.getProdRelDocsDir());
					System.out.println("Created zip file");
				}
			} 	
			if (fileName.length() < 0)
				System.out.println("File name is not specified");
			if (new File(fileFullName).exists())
				System.out.println("Created zip file in " + fileFullName);
		}
}


