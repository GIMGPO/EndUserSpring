package trisoftdp.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProductGroupData {

	private static DocumentBuilderFactory dbf;
	static {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setCoalescing(false);
		dbf.setExpandEntityReferences(true);
	}
	/**
	 * Retrieves the data from xmlProductGroups config file.
	 * 
	 * @param xmlProductGroups
	 * @return
	 * @throws DynException
	 */
	public static DynProductGroup[] getAllProductGroups (File xmlProductGroups) throws DynException {
		DynProductGroup[] prodGroups = null;
		DocumentBuilder builder;
		Document d;
		NodeList nodeList0, nodeList1;
		Node node0, node1;
		try {
			System.out.println("parsing file: " + xmlProductGroups);
			builder = dbf.newDocumentBuilder();
			if(!xmlProductGroups.canRead())
				throw new IOException("cannot read " + xmlProductGroups);
			d = builder.parse(xmlProductGroups);
			Element root = d.getDocumentElement();
			if (!root.getNodeName().equals("productGroups"))
				return null;						
			nodeList0 = root.getElementsByTagName("productGroup");
			prodGroups = new DynProductGroup[nodeList0.getLength()];
			System.out.println("number of packages: " + nodeList0.getLength());
			for(int i =0; i < nodeList0.getLength(); i++) {
				node0 = nodeList0.item(i);
				DynProductGroup prodGroup = new DynProductGroup();
				prodGroup.prodFolder = ((Element) node0).getAttribute("folderName");
				System.out.println("\tFolder: " + prodGroup.prodFolder);
				if ("yes".equals(((Element) node0).getAttribute("singleGroup"))) {
					prodGroup.singleGroup = true;
				} else {
					prodGroup.singleGroup = false;
					prodGroup.multiGroupNote = ((Element) node0).getElementsByTagName("multiGroupNote").item(0).getTextContent();
				}
				//prodGroup.singleGroup = ("yes".equals(((Element) node0).getAttribute("singleGroup"))) ? true: false;
				prodGroup.prodNum = Integer.parseInt(((Element) node0).getAttribute("prodNum"));
				prodGroup.prodTitle = ((Element) node0).getElementsByTagName("productTitle").item(0).getTextContent();
				prodGroup.prodNote = ((Element) node0).getElementsByTagName("productNote").item(0).getTextContent();
				
				nodeList1 = ((Element) node0).getElementsByTagName("productPage");
				prodGroup.pages = new DynProductGroup.Page[nodeList1.getLength()];

				for(int j = 0; j < nodeList1.getLength(); j++) {
					DynProductGroup.Page page = new DynProductGroup.Page();
					node1 = nodeList1.item(j);
					page.pageKey = ((Element) node1).getAttribute("key");
					page.pageNum = Integer.parseInt(((Element) node1).getAttribute("order"));
					page.pageType = DynProductGroup.PAGE_TYPE.valueOf(((Element) node1).getAttribute("type"));
					page.pageName = ((Element) node1).getElementsByTagName("pageName").item(0).getTextContent();
					page.pageDesc = ((Element) node1).getElementsByTagName("pageDesc").item(0).getTextContent();
					prodGroup.pages[j] = page;
				}
				prodGroups[i] = prodGroup;
			} 
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		}

		return prodGroups;
	}
	
	public static Properties getProductProperties(String prodFolderName) throws DynException {
		FileInputStream is = null;
		Properties prodProps = null;
		File propsFile = new File(CoreConstants.appPropsMap.get("CONFIG_DIR") + File.separator + "endUser_" + prodFolderName + ".properties");
		//System.out.println("propsFile = " + propsFile);
		try {
			prodProps = new Properties();
			is = new FileInputStream(propsFile);
			prodProps.load(is);
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if(is != null) try {is.close();} catch(IOException e) {e.printStackTrace();}
        }
		return prodProps;
	}
}
