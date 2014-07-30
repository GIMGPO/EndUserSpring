package trisoftdp.core;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DownloadAllRelDocs {

	public List<String> parseRelatedDocsXml(String xmlPath)
	{
		List<String> fileList = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	 
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is = new FileInputStream(xmlPath);
			Document document = builder.parse(is);

			NodeList nodeList = document.getElementsByTagName("doc");
			for(int x=0,size= nodeList.getLength(); x<size; x++) {
				String filename = nodeList.item(x).getAttributes().getNamedItem("file").getNodeValue();
				if (!filename.endsWith(".zip"));
					fileList.add(nodeList.item(x).getAttributes().getNamedItem("file").getNodeValue());
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
	}

	public void zipFiles(List<String> files, String zipFileName, String fullCachePath, String fullRelDocsPath){

		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;

		try {
			fos = new FileOutputStream(fullCachePath + zipFileName);
			zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			for(String file: files){
				File input = new File(fullRelDocsPath + file);
				if (input.exists()) {
					fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4*1024];
					int size = 0;
					while((size = fis.read(tmp)) != -1)
						zipOut.write(tmp, 0, size);
					zipOut.flush();
					fis.close();
				} else {
					CoreConstants.logger.info("Cannot zip all: " + file + " does not exists");
				}
			}
			zipOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fos != null)    try{ fos.close();}    catch(IOException e) {e.printStackTrace();}
			if(fis != null)    try{ fis.close();}    catch(IOException e) {e.printStackTrace();}
			if(zipOut != null) try{ zipOut.close();} catch(IOException e) {e.printStackTrace();}
		}
	}

}
