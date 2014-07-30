/**
 * Set of methods related to the DynamicPublishingPackage
 * 
 * Updated Feb 2014:
 * 1. New method get DynamicPage(File) is added
 * 2. New attribute added in getPackage(File) to the class ProfileValue(), multiselect (Lines 364-368).
 * 
 * Method getAllGroups(File) is not used anymore - deprecated.
 * 
 * @author shadrn1
 */

package trisoftdp.web.core;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import trisoftdp.core.DynException;
import trisoftdp.core.DynPackageDescriptor;
import trisoftdp.core.DynPage;
import trisoftdp.core.DynRelatedDocs;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.ToolKit;
import trisoftdp.core.DynPage.DynPackageGroup;
import trisoftdp.core.DynPage.DynPackageGroup.Pack;
import trisoftdp.core.DynRelatedDocs.DocGroup;
import trisoftdp.core.DynRelatedDocs.FILE_TYPE;
import trisoftdp.core.DynRelatedDocs.RelDoc;
import trisoftdp.core.DynamicPublishingPackage.DitaMap;
import trisoftdp.core.DynamicPublishingPackage.MapProfile;
import trisoftdp.core.DynamicPublishingPackage.PROFILE_SELECT;
import trisoftdp.core.DynamicPublishingPackage.PROFILE_STATUS;
import trisoftdp.core.DynamicPublishingPackage.Profile;
import trisoftdp.core.DynamicPublishingPackage.ProfileValue;
import trisoftdp.core.DynamicPublishingPackage.REVERSE;


public class PackageData {
	private static DocumentBuilderFactory dbf;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	static {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setCoalescing(false);
		dbf.setExpandEntityReferences(true);
	}

	public static void validateUserPack(DynamicPublishingPackage pack) throws ServletException {
//!!!!		for()
		for(Profile p : pack.profiles) {
			if(p.id == null)
				throw new ServletException("profile " + p.name + " does not have id");
			if(p.values.length == 0)
				throw new ServletException("profile does not have values");
			for(ProfileValue v: p.values)
				if( v.id == null)
					throw new ServletException("value does not have id");
		}
	}

	public static String packageToXML(DynamicPublishingPackage pack) {
		//		!!! DitaMapProfiles are not added !!!		
		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<!DOCTYPE dynamicPublishingPackage SYSTEM \"esa_dynamic_publishing.dtd\">\n");
		buf.append("<dynamicPublishingPackage xml:lang=\"en_us\">\n");
		buf.append(" <header>\n");
		buf.append("  <packageType>" + escapeXML(pack.pubPackage) + "</packageType>\n");
		buf.append("  <packageName>" + escapeXML(pack.packageName) + "</packageName>\n");
		buf.append("  <productRelease>" + escapeXML(pack.productRelease) + "</productRelease>\n");
		//		buf.append("  <createDate>" + dateFormat.format(pack.createDate)+ "</createDate>\n");
		buf.append("  <documentsRequested>\n");
		for(DynamicPublishingPackage.DitaMap dm: pack.ditaMaps) {
			buf.append("   <ditaMap ditaMapFile=\"" + dm.file + "\">\n");
			buf.append("    <ditaMapTitle>" + escapeXML(dm.title)+ "</ditaMapTitle>\n");
			if(dm.task != null)
				buf.append("    <ditaMapTask>" + escapeXML(dm.task) + "</ditaMapTask>\n");
			buf.append("   </ditaMap>\n");

		}
		buf.append("  </documentsRequested>\n");
		buf.append("  <languagesRequested>\n");
		for(String l: pack.languages)
			buf.append("   <language>"+ l + "</language>\n");
		buf.append("  </languagesRequested>\n");
		//buf.append("  <comments>" +((pack.comments == null)?  "":escapeXML(pack.comments)) + "</comments>\n");
		buf.append(" </header>\n");
		buf.append(" <profilingAttributes>\n");
		for(DynamicPublishingPackage.Profile p: pack.profiles) {
			buf.append("  <profilingAttribute profAttrId=\""  + p.id + "\" profAttrStatus=\"" + p.status.name()+ "\" profAttrSelect=\"" + p.selectType.name() + "\">\n");
			buf.append("   <profAttrName>" + p.name + "</profAttrName>\n");
			buf.append("   <profAttrQuesNum>" + p.quesNum + "</profAttrQuesNum>\n");
			buf.append("   <profAttrQuesString>" + escapeXML(p.quesString)+ "</profAttrQuesString>\n");
			buf.append("   <profAttrValues>\n");
			for(DynamicPublishingPackage.ProfileValue v: p.values) {
				buf.append("    <profAttrValue profAttrValId=\"" + v.id + "\">\n");
				buf.append("     <profAttrValueName>" + v.name + "</profAttrValueName>\n");
				buf.append("     <profAttrValueDependencies>\n");
				for(String s: v.dependentIds) {
					buf.append("      <profAttrValDependency profAttrRefId=\"" + p.id + "\">\n");
					buf.append("       <profAttrValueDenpendent profAttrValRefId=\"" + s + "\" />\n");
					buf.append("      </profAttrValDependency>\n");
				}
				buf.append("     </profAttrValueDependencies>\n");
				buf.append("    </profAttrValue>\n");
			}
			buf.append("   </profAttrValues>\n");
			buf.append("  </profilingAttribute>\n");
		}
		buf.append(" </profilingAttributes>\n");
		buf.append("</dynamicPublishingPackage>\n");
		return buf.toString();
	}

	/**
	 * Reads page configuration xml file into the DynPage object.
	 * 
	 * 
	 * @param xmlGroups - configuration file (ConfigGroups_XXX.xml, located in the product group config folder, full path)
	 * @return DynPage object, see DynPage.java
	 * @throws DynException
	 */
	public static DynPage getDynamicPage (File xmlGroups) throws DynException {
		DynPage dynPage = null;
		DocumentBuilder builder;
		Document d;
		NodeList nodeList0, nodeList1;
		Node node0, node1;
		try {
			builder = dbf.newDocumentBuilder();
			d = builder.parse(xmlGroups);
			//parsing xmlConfig			
			Element root = d.getDocumentElement();
			
			if (!root.getNodeName().equals("configGroups"))
				return null;	
			dynPage = new DynPage();
//			dynPage.pageTitle = root.getElementsByTagName("cgTitle").item(0).getTextContent();
//			dynPage.pageGroupsTitle = root.getElementsByTagName("cgGroupsTitle").item(0).getTextContent();
//			dynPage.pageIntro = root.getElementsByTagName("cgIntro").item(0).getTextContent();
			
			dynPage.pageTitle =  root.getElementsByTagName("cgTitle").item(0).getNodeValue();
			dynPage.pageGroupsTitle = root.getElementsByTagName("cgGroupsTitle").item(0).getNodeValue();
			dynPage.pageIntro = root.getElementsByTagName("cgIntro").item(0).getNodeValue();
			
			nodeList0 = root.getElementsByTagName("packGroup");
			dynPage.packGroups = new DynPage.DynPackageGroup[nodeList0.getLength()];

			for(int i =0; i < nodeList0.getLength(); i++) {
				node0 = nodeList0.item(i);
				DynPage.DynPackageGroup group = new DynPage.DynPackageGroup();
				group.groupName = ((Element) node0).getAttribute("groupName");
				group.groupNum = Integer.parseInt(((Element) node0).getAttribute("groupNum"));
				nodeList1 = ((Element) node0).getElementsByTagName("pack");
				group.packs = new DynPage.DynPackageGroup.Pack[nodeList1.getLength()];

				for(int j = 0; j < nodeList1.getLength(); j++) {
					DynPage.DynPackageGroup.Pack pack = new DynPage.DynPackageGroup.Pack();
					node1 = nodeList1.item(j);
					pack.packFileName = ((Element) node1).getAttribute("confId");
					System.out.println("\t\t" + pack.packFileName);
					pack.packNum = Integer.parseInt(((Element) node1).getAttribute("packNum"));
					group.packs[j] = pack;
				}
				dynPage.packGroups[i] = group;
			} 
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		}
		return dynPage;
	}

	/**
	 * Deprecated method - now a part of the getDynamicPage method.
	 * 
	 * @param xmlGroups
	 * @return
	 * @throws DynException
	 */
	
	public static DynPackageGroup[] getAllGroups (File xmlGroups) throws DynException {
		DynPackageGroup[] groups = null;
		DocumentBuilder builder;
		Document d;
		NodeList nodeList0, nodeList1;
		Node node0, node1;
		try {
			System.out.println("parsing file: " + xmlGroups);
			builder = dbf.newDocumentBuilder();
			if(!xmlGroups.canRead())
				throw new IOException("cannot read " + xmlGroups);
			d = builder.parse(xmlGroups);
			Element root = d.getDocumentElement();
			if (!root.getNodeName().equals("configGroups"))
				return null;						
			nodeList0 = root.getElementsByTagName("packGroup");
			groups = new DynPackageGroup[nodeList0.getLength()];
			System.out.println("number of packages: " + nodeList0.getLength());
			for(int i =0; i < nodeList0.getLength(); i++) {
				node0 = nodeList0.item(i);
				DynPackageGroup group = new DynPackageGroup();
				group.groupName = ((Element) node0).getAttribute("groupName");
				System.out.println("\t" + group.groupName);
				group.groupNum = Integer.parseInt(((Element) node0).getAttribute("groupNum"));
				nodeList1 = ((Element) node0).getElementsByTagName("pack");
				group.packs = new DynPackageGroup.Pack[nodeList1.getLength()];

				for(int j = 0; j < nodeList1.getLength(); j++) {
					DynPackageGroup.Pack pack = new DynPackageGroup.Pack();
					node1 = nodeList1.item(j);
					pack.packFileName = ((Element) node1).getAttribute("confId");
					System.out.println("\t\t" + pack.packFileName);
					pack.packNum = Integer.parseInt(((Element) node1).getAttribute("packNum"));
					group.packs[j] = pack;
				}
				groups[i] = group;
			} 
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		}

		return groups;
	}

	public static DynPackageDescriptor getPackageDescriptor(File xmlConfig) throws DynException {
		DynPackageDescriptor packInfo = null;
		DocumentBuilder builder = null;
		Document d = null;
		NodeList nodeList;
		try {
			builder = dbf.newDocumentBuilder();
			d = builder.parse(xmlConfig);
			//parsing xmlConfig			
			Element root = d.getDocumentElement();

			if (!root.getNodeName().equals("dynamicPublishingPackage"))
				return null;
			packInfo = new DynPackageDescriptor();
//			packInfo.pubPackage = root.getElementsByTagName("packageType").item(0).getTextContent();
//			packInfo.packageName = root.getElementsByTagName("packageName").item(0).getTextContent();
//			packInfo.productRelease = root.getElementsByTagName("productRelease").item(0).getTextContent();
//			packInfo.comments = root.getElementsByTagName("comments").item(0).getTextContent();		
			packInfo.pubPackage = root.getElementsByTagName("packageType").item(0).getNodeValue();
			packInfo.packageName = root.getElementsByTagName("packageName").item(0).getNodeValue();
			packInfo.productRelease = root.getElementsByTagName("productRelease").item(0).getNodeValue();
			packInfo.comments = root.getElementsByTagName("comments").item(0).getNodeValue();
			
			try {	
				nodeList = ((Element) root.getElementsByTagName("ditaMap")).getElementsByTagName("ditaMapProfile");
				if (nodeList.getLength()>0)
					packInfo.quesFirst = DynamicPublishingPackage.REVERSE.yes;
			} catch (Exception e) {
				packInfo.quesFirst = DynamicPublishingPackage.REVERSE.no;
			}
			try {
				packInfo.link = ((Element) root.getElementsByTagName("linkUrl").item(0)).getAttribute("url");
				packInfo.relDocXml =((Element) root.getElementsByTagName("linkUrl").item(0)).getAttribute("relDocXmlFile");
			} catch (Exception e) {}

		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new DynException("IOException: " + e.getMessage());
		}
		return packInfo;
	}

	public static DynamicPublishingPackage getPackage(File xmlFile) throws DynException {
		//System.out.println("reading " + xmlFile);
		DynamicPublishingPackage pack = null;
		DocumentBuilder builder = null;
		Document d = null;
		try {
			builder = dbf.newDocumentBuilder();
			System.out.println("parsing file: " + xmlFile);
			d = builder.parse(xmlFile);
			Element root = d.getDocumentElement();
			Node node0, node1;
			NodeList nodeList0, nodeList1, nodeList2;
			if (!root.getNodeName().equals("dynamicPublishingPackage"))
				return null;
			pack = new DynamicPublishingPackage();
//			pack.pubPackage = root.getElementsByTagName("packageType").item(0).getTextContent();
//			pack.packageName = root.getElementsByTagName("packageName").item(0).getTextContent();
//			pack.productRelease = root.getElementsByTagName("productRelease").item(0).getTextContent();
//			String dateStr = root.getElementsByTagName("createDate").item(0).getTextContent();
//			
			pack.pubPackage = root.getElementsByTagName("packageType").item(0).getNodeValue();
			pack.packageName = root.getElementsByTagName("packageName").item(0).getNodeValue();
			pack.productRelease = root.getElementsByTagName("productRelease").item(0).getNodeValue();
			String dateStr = root.getElementsByTagName("createDate").item(0).getNodeValue();
			pack.createDate = dateFormat.parse(dateStr);
			nodeList0 = root.getElementsByTagName("ditaMap");
			pack.ditaMaps = new DynamicPublishingPackage.DitaMap[nodeList0.getLength()];
			for(int i = 0; i < nodeList0.getLength(); i++) {
				node0 = nodeList0.item(i);
				pack.ditaMaps[i] = new DynamicPublishingPackage.DitaMap();
				pack.ditaMaps[i].file = ((Element) node0).getAttribute("ditaMapFile");
//				pack.ditaMaps[i].title = ((Element) node0).getElementsByTagName("ditaMapTitle").item(0).getTextContent();
				pack.ditaMaps[i].title = ((Element) node0).getElementsByTagName("ditaMapTitle").item(0).getNodeValue();
				try {
//					pack.ditaMaps[i].task = ((Element) node0).getElementsByTagName("ditaMapTask").item(0).getTextContent();
					pack.ditaMaps[i].task = ((Element) node0).getElementsByTagName("ditaMapTask").item(0).getNodeValue();
				} catch (Exception e) {e.printStackTrace();}
				try {	
					nodeList1 = ((Element) node0).getElementsByTagName("ditaMapProfile");
					pack.ditaMaps[i].mapProfiles = new DynamicPublishingPackage.MapProfile[nodeList1.getLength()];
					for(int j = 0; j < nodeList1.getLength(); j++) {
						node1 = nodeList1.item(j);
						pack.ditaMaps[i].mapProfiles[j] = new DynamicPublishingPackage.MapProfile();
						pack.ditaMaps[i].mapProfiles[j].name = ((Element) node1).getAttribute("name");
						pack.ditaMaps[i].mapProfiles[j].values = ((Element) node1).getAttribute("value").split(" ");
					}
					if (nodeList1.getLength()>0)
						pack.quesFirst = DynamicPublishingPackage.REVERSE.yes;
				} catch (Exception e) {
					pack.quesFirst = DynamicPublishingPackage.REVERSE.no;
				}
			}
			Arrays.sort(pack.ditaMaps, new MapComparatorByTaskOrTitle());
			nodeList0 = root.getElementsByTagName("language");
			pack.languages = new String[nodeList0.getLength()];
			for(int i = 0; i < nodeList0.getLength(); i++)
//				pack.languages[i] = nodeList0.item(i).getTextContent();
				pack.languages[i] = nodeList0.item(i).getNodeValue();
			Arrays.sort(pack.languages);

			nodeList0 = root.getElementsByTagName("profilingAttribute");
			pack.profiles = new DynamicPublishingPackage.Profile[nodeList0.getLength()];
			for(int i = 0; i < nodeList0.getLength(); i++) {
				node0 = nodeList0.item(i);
				pack.profiles[i] = new DynamicPublishingPackage.Profile();
				pack.profiles[i].id = ((Element) node0).getAttribute("profAttrId");
				try {
					pack.profiles[i].status = PROFILE_STATUS.valueOf(((Element) node0).getAttribute("profAttrStatus"));
				} catch (Exception e) {
					pack.profiles[i].status = PROFILE_STATUS.normal;
				}
				try {
					pack.profiles[i].selectType = DynamicPublishingPackage.PROFILE_SELECT.valueOf(((Element) node0).getAttribute("profAttrSelect"));
				} catch (Exception e) {
					pack.profiles[i].selectType = DynamicPublishingPackage.PROFILE_SELECT.user;
				}
//				pack.profiles[i].name = ((Element) node0).getElementsByTagName("profAttrName").item(0).getTextContent();
				pack.profiles[i].name = ((Element) node0).getElementsByTagName("profAttrName").item(0).getNodeValue();
				try {
//					pack.profiles[i].quesString = ((Element) node0).getElementsByTagName("profAttrQuesString").item(0).getTextContent();
					pack.profiles[i].quesString = ((Element) node0).getElementsByTagName("profAttrQuesString").item(0).getNodeValue();
				} catch (Exception e) {}
				try {
//					pack.profiles[i].quesInfo = ((Element) node0).getElementsByTagName("profAttrQuesInfo").item(0).getTextContent();
					pack.profiles[i].quesInfo = ((Element) node0).getElementsByTagName("profAttrQuesInfo").item(0).getNodeValue();
				} catch (Exception e) {}
				try {
//					pack.profiles[i].quesNum = Integer.parseInt(((Element) node0).getElementsByTagName("profAttrQuesNum").item(0).getTextContent());
					pack.profiles[i].quesNum = Integer.parseInt(((Element) node0).getElementsByTagName("profAttrQuesNum").item(0).getNodeValue());
				} catch (Exception e) {}
				nodeList1 = ((Element) node0).getElementsByTagName("profAttrValue");
				pack.profiles[i].values = new DynamicPublishingPackage.ProfileValue[nodeList1.getLength()];
				for(int j = 0; j < nodeList1.getLength(); j++) {
					node1 = nodeList1.item(j);
					pack.profiles[i].values[j] = new DynamicPublishingPackage.ProfileValue();
					pack.profiles[i].values[j].id = ((Element) node1).getAttribute("profAttrValId");
					try {
						pack.profiles[i].values[j].multiselect = ("yes".equals(((Element) node1).getAttribute("profMultiSelectNext")))? true : false;
					} catch (Exception e) {
						pack.profiles[i].values[j].multiselect = false;
					}			
//					pack.profiles[i].values[j].name = ((Element) node1).getElementsByTagName("profAttrValueName").item(0).getTextContent();
					pack.profiles[i].values[j].name = ((Element) node1).getElementsByTagName("profAttrValueName").item(0).getNodeValue();					
					nodeList2 = ((Element) node1).getElementsByTagName("profAttrValDependency");
					pack.profiles[i].values[j].dependentIds = new String[nodeList2.getLength()];
					for(int k = 0; k < nodeList2.getLength(); k++)
						pack.profiles[i].values[j].dependentIds[k] = ((Element)((Element) nodeList2.item(k)).getElementsByTagName("profAttrValueDenpendent").item(0)).getAttribute("profAttrValRefId");
				}
				Arrays.sort(pack.profiles[i].values, new ProfileValueComparator());
			}
			Arrays.sort(pack.profiles, new ProfileComparator());
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		} catch (ParseException e1) {
			throw new DynException("ParseException (Date): " + e1.getMessage());
		}
		return pack;
	}



	public static Map<String,String> getProfileAliases(File xmlFile) throws DynException {
		HashMap<String,String> map = new HashMap<String,String>();
		DocumentBuilder builder = null;
		Document d = null;
		String key, value;
		try {
			builder = dbf.newDocumentBuilder();
			System.out.println("parsing file in getProfileAliases: " + xmlFile);
			d = builder.parse(xmlFile);
			Element root = d.getDocumentElement();
			Node node;
			NodeList nodeList;
			if (!root.getNodeName().equals("profileAliases"))
				return null;
			nodeList = root.getElementsByTagName("profile");
			for(int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				key = ((Element) node).getAttribute("name");

				value = ((Element) node).getAttribute("alias");
				map.put(key, value);
			}

			nodeList = root.getElementsByTagName("attr");
			for(int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
				key = ((Element) node).getAttribute("name");
				value = ((Element) node).getAttribute("alias");
				map.put(key, value);
			}
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		}
		return map;
	}

	public static DynamicPublishingPackage.Profile[] getVisibleProfiles(DynamicPublishingPackage pack) {
		List<DynamicPublishingPackage.Profile> prList  = new ArrayList<DynamicPublishingPackage.Profile>();
		for (DynamicPublishingPackage.Profile p: pack.profiles) {
			if( p.status != DynamicPublishingPackage.PROFILE_STATUS.hidden )
				prList.add(p);
		}
		DynamicPublishingPackage.Profile[] visibleProfiles = new DynamicPublishingPackage.Profile[prList.size()];
		prList.toArray(visibleProfiles);
		return visibleProfiles;
	}

	public static Map<String,String[]> getDependencies(DynamicPublishingPackage pack) {
		Map<String,String[]> dependIds = new HashMap<String,String[]>();
		if (pack.profiles.length != 0) {
			for (int i=0; i<pack.profiles.length; i++) {
				if (pack.profiles[i].values.length != 0) {
					for (int j=0; j<pack.profiles[i].values.length; j++) {
						String key = pack.profiles[i].values[j].id;
						String[] values = pack.profiles[i].values[j].dependentIds;
						dependIds.put(key,values);
					}
				}
			}
		}
		return dependIds;
	}

	public static String getDepend(DynamicPublishingPackage pack) {
		Map<String,String[]> dependIds = getDependencies(pack);
		StringBuilder buf = new StringBuilder("var depend = new Array();");
		for(Entry<String, String[]> entry: dependIds.entrySet()) {
			buf.append("\n depend['" + entry.getKey() + "'] = new Array();");
			for (int k=0; k<entry.getValue().length; k++)
				buf.append("\n\t depend['" + entry.getKey() + "'].push('" + entry.getValue()[k] + "');");
		}
		return buf.toString();
	}

	public static DynamicPublishingPackage.DitaMap[] getAvailableMaps (DynamicPublishingPackage dPack, DynamicPublishingPackage uPack) {
		DynamicPublishingPackage.Profile[] uPackVisProfiles = getVisibleProfiles(uPack);
		if (DynamicPublishingPackage.REVERSE.yes.equals(dPack.quesFirst)) {
			List<DynamicPublishingPackage.DitaMap> am = new ArrayList<DynamicPublishingPackage.DitaMap>();
			for (DynamicPublishingPackage.DitaMap dm: dPack.ditaMaps) {
				for (DynamicPublishingPackage.MapProfile dmp: dm.mapProfiles) {
					if (dmp.name.equals(uPackVisProfiles[0].id)) {
						for (String dmpv: dmp.values)
							for (DynamicPublishingPackage.ProfileValue upv: uPackVisProfiles[0].values)
								if (upv.id.equals(dmpv))
									if(!am.contains(dm)) am.add(dm);

					}
				}
			}
			for (int i=1; i<uPackVisProfiles.length; i++ ) {
				List<DynamicPublishingPackage.DitaMap> iMaps = new ArrayList<DynamicPublishingPackage.DitaMap>();
				for (DynamicPublishingPackage.DitaMap dm: dPack.ditaMaps) {
					for (DynamicPublishingPackage.MapProfile dmp: dm.mapProfiles) {
						if (dmp.name.equals(uPackVisProfiles[i].id)) {
							for (String dmpv: dmp.values)
								for (DynamicPublishingPackage.ProfileValue upv: uPackVisProfiles[i].values)
									if (upv.id.equals(dmpv))
										if(!iMaps.contains(dm)) iMaps.add(dm);

						}
					}
				}
				am = ToolKit.getCommonArrayElements(am, iMaps);
			}
			DynamicPublishingPackage.DitaMap[] availableMaps = new DynamicPublishingPackage.DitaMap[am.size()];
			am.toArray(availableMaps);
			return availableMaps;
		} else 
			return dPack.ditaMaps;			
	}

	public static DynRelatedDocs getRelatedDocs(File xmlFile) throws DynException {
		DynRelatedDocs dynRelDocs = null;
		DocumentBuilder builder = null;
		Document d = null;
		try {
			builder = dbf.newDocumentBuilder();
			d = builder.parse(xmlFile);			
		} catch (ParserConfigurationException e) {
			throw new DynException("ParserConfigurationException: " + e.getMessage());
		} catch (SAXException e) {
			throw new DynException("SAXException: " + e.getMessage());
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		}	
		Element root = d.getDocumentElement();
		Node node0, node1;
		NodeList nodeList0, nodeList1;
		if (!root.getNodeName().equals("relatedDocs"))
			return null;
		dynRelDocs = new DynRelatedDocs();
//		dynRelDocs.rdTitle = root.getElementsByTagName("rdTitle").item(0).getTextContent();
		dynRelDocs.rdTitle = root.getElementsByTagName("rdTitle").item(0).getNodeValue();		
		nodeList0 = root.getElementsByTagName("group");
		dynRelDocs.docGroups = new DynRelatedDocs.DocGroup[nodeList0.getLength()];
		for(int i = 0; i < nodeList0.getLength(); i++) {
			node0 = nodeList0.item(i);
			dynRelDocs.docGroups[i] = new DynRelatedDocs.DocGroup();
			dynRelDocs.docGroups[i].groupName = ((Element) node0).getAttribute("groupName");
			dynRelDocs.docGroups[i].groupNum = Integer.parseInt(((Element) node0).getAttribute("groupNum"));
			nodeList1 = ((Element) node0).getElementsByTagName("doc");
			dynRelDocs.docGroups[i].relDocs = new DynRelatedDocs.RelDoc[nodeList1.getLength()];
			for(int j = 0; j < nodeList1.getLength(); j++) {
				node1 = nodeList1.item(j);
				dynRelDocs.docGroups[i].relDocs[j] = new DynRelatedDocs.RelDoc();
				dynRelDocs.docGroups[i].relDocs[j].docNum = Integer.parseInt(((Element) node1).getAttribute("docNum"));
				dynRelDocs.docGroups[i].relDocs[j].docFile = ((Element) node1).getAttribute("file");
				dynRelDocs.docGroups[i].relDocs[j].helpFolder = ((Element) node1).getAttribute("helpFolder");
				dynRelDocs.docGroups[i].relDocs[j].helpFile = ((Element) node1).getAttribute("helpFile");
//				dynRelDocs.docGroups[i].relDocs[j].docName = node1.getTextContent();
				dynRelDocs.docGroups[i].relDocs[j].docName = node1.getNodeValue();				
				dynRelDocs.docGroups[i].relDocs[j].fileType = (dynRelDocs.docGroups[i].relDocs[j].docFile.toUpperCase().endsWith(".ZIP"))? 
						DynRelatedDocs.FILE_TYPE.zip:DynRelatedDocs.FILE_TYPE.pdf;
			}
			Arrays.sort(dynRelDocs.docGroups[i].relDocs, new DocComparator());
		}
		Arrays.sort(dynRelDocs.docGroups, new DocGroupComparator()); 
		return dynRelDocs;
	}

	public static String escapeXML(String aTagFragment) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			switch (character) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '\"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&apos;");
				break;
			case '&':
				result.append("&amp;");
				break;
			default:
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	static class ProfileComparator implements Serializable, Comparator<Profile> {
		private static final long serialVersionUID = 1L;

		//		@Override
		public int compare(Profile p1, Profile p2) {
			return p1.quesNum - p2.quesNum;
		}
	}
	static class ProfileValueComparator implements Serializable, Comparator<ProfileValue> {
		private static final long serialVersionUID = 1L;

//		@Override
		public int compare(ProfileValue pv1, ProfileValue pv2) {
			String value1 = pv1.name;
			String value2 = pv2.name;
			return value1.compareTo(value2);
		}
	}
	static class DocGroupComparator implements Serializable, Comparator<DocGroup> {
		private static final long serialVersionUID = 1L;
//		@Override
		public int compare(DocGroup dg1, DocGroup dg2) {
			return dg1.groupNum - dg2.groupNum;
		}
	}
	static class DocComparator implements Serializable, Comparator<RelDoc> {
		private static final long serialVersionUID = 1L;
//		@Override
		public int compare(RelDoc rd1, RelDoc rd2) {
			return rd1.docNum - rd2.docNum;
		}
	}
	static class MapComparatorByTaskOrTitle implements Serializable, Comparator<DitaMap> {
		private static final long serialVersionUID = 1L;

		//		@Override
		public int compare(DitaMap dm1, DitaMap dm2) {
			String task1 = (dm1.task != null) ? dm1.task.toLowerCase() : dm1.title.toLowerCase();
			String task2 = (dm2.task != null) ? dm2.task.toLowerCase() : dm2.title.toLowerCase();
			return task1.compareTo(task2);
		}

	}

}


