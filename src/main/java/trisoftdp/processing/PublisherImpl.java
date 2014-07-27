package trisoftdp.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.LocaleUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.DynamicPublishingPackage.Profile;
import trisoftdp.core.DynamicPublishingPackage.ProfileValue;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.ToolKit;
import trisoftdp.core.UserBean;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class PublisherImpl implements Publisher {

//	@Override
	public void process(long id, String configId, String contentDir, String configDir,
			DynamicPublishingPackage pack, Map<String, String> legend,
			String lang) throws DynException, IOException {
		File srcDir, profilesXML;

		srcDir = new File(contentDir + configId 
				+ File.separator + pack.ditaMaps[0].title);

		profilesXML = new File(configDir + CoreConstants.appPropsMap.get("PROFILE_ALIASES_XML"));

		File targetDir = targetDir(id, pack.ditaMaps[0].title);
		createDirToProcess(targetDir, srcDir, new File(configDir), profilesXML, pack, legend, id,
				lang);
		CoreConstants.logger.info("Running Ant...");
		runAnt(targetDir, id);
	}

//	@Override
/*	public long process(String configId, String contentDir, String configDir, 
			DynamicPublishingPackage pack, Map<String,String> legend, 
			String lang, ProdEnvBean prodEnv) throws DynException, IOException {*/
	public long process(MementoUserBean user, ProdEnvBean prodEnv, String lang) throws DynException, IOException {
		long id = ToolKit.generateId();
		DynamicPublishingPackage pack = user.getUserPack();
		String configId = user.getConfigId();
		String contentDir = prodEnv.getProdContentDir();
		String configDir = prodEnv.getProdConfigDir();
		Map<String,String> legend = user.getPubLegend();
		File targetDir = targetDir(id, pack.ditaMaps[0].title);
		File failedJobsDir = failedJobsDir(id, pack.ditaMaps[0].title);
		process(id, configId, contentDir, configDir, pack, legend, lang);
		CoreConstants.logger.info("Processing is done. Creating result now...");
		createResult(targetDir, failedJobsDir, pack.outputType, id, pack.ditaMaps[0].title);
		if("yes".equals(prodEnv.getProdCleanAfter())) {
			CoreConstants.logger.info("Cleaning up after publishing...");
			ToolKit.deleteDir(targetDir);
		}
		return id;	
	}
//	@Override
	public void processStatic(long id, MementoUserBean user, 
			ProdEnvBean prodEnv, String lang, File uploadedFile) throws DynException, IOException {
		File profilesXML;
		//String locFolder = CoreConstants.languagesMap.get(lang);
		DynamicPublishingPackage pack = user.getUserPack();
		//String configId = user.getConfigId();
		//String contentDir = prodEnv.getProdContentDir();
		File configDir = new File(prodEnv.getProdConfigDir());
		profilesXML = new File(configDir + File.separator + CoreConstants.appPropsMap.get("PROFILE_ALIASES_XML"));
		Map<String,String> legend = user.getPubLegend();
		File targetDir = targetDir(id, pack.ditaMaps[0].title);
		File resultFile = new File("" + id + "_" + pack.ditaMaps[0].title + ".pdf");
		//File failedJobsDir = failedJobsDir(id, pack.ditaMaps[0].title);
		
		createDirToProcessStatic(targetDir, resultFile, configDir, profilesXML, pack, legend, id,
				lang, uploadedFile);
		CoreConstants.logger.info("Getting the legend attached...");
		//runStaticAnt(targetDir, id, locFolder, pack.ditaMaps[0].title, uploadedFile);
		runAnt(targetDir, id);
	}


	private void createDirToProcessStatic(File targetDir, File resultFile, File configDir, File profilesXML, 
			DynamicPublishingPackage pack, Map<String, String> legend, 
			long id, String lang, File uploadedFile) {
		File copyOutFolder = new File(CoreConstants.appPropsMap.get("STATIC_OUT_COPY"));
		File outFolder = new File(targetDir + File.separator + "output");
		File fobFolder = new File(targetDir + File.separator + "fop");
		File targetFile = new File(targetDir + File.separator + uploadedFile);
		
		File tmpFile = new File(CoreConstants.appPropsMap.get("TMP_DIR") + File.separator + uploadedFile);
		boolean success = targetDir.mkdir();
		if (success) {
			try {
				if(!fobFolder.mkdir()) 
					CoreConstants.logger.severe("Failed to create " + fobFolder);
				ToolKit.copyDirectory(copyOutFolder, outFolder);
				ToolKit.copyDirectory(tmpFile, targetFile);
				//createLegend(targetDir, profilesXML, legend, id, lang);
				//createDitaval(targetDir, pack);
				createStaticProperty(targetDir, configDir, targetFile, resultFile, pack, lang);
				createLegend(targetDir, profilesXML, legend, id, lang);
				CoreConstants.logger.info("The work folder is set: " + targetDir.getAbsolutePath());

			} catch (IOException e) {
				CoreConstants.logger.severe("IOException: " + e.getMessage());
				e.printStackTrace();
			}
		}

	}
	
	private static void createStaticProperty(File targetDir, File configDir, File targetFile, File resultFile, DynamicPublishingPackage pack, String lang) {
		Properties buildProps = convertResourceBundleToProperties(ResourceBundle.getBundle(CoreConstants.TEMP_BUILD));
		String transtype = ("pdf2".equals(pack.outputType.toString()))? CoreConstants.appPropsMap.get("PDF_OUTPUT_TRANSTYPE"): pack.outputType.toString();
		buildProps.put("transtype", transtype);
		buildProps.put("basedir", targetDir.getAbsolutePath());
		buildProps.put("input.dir", targetDir.getAbsolutePath());
		buildProps.put("output.dir", targetDir.getAbsolutePath() + File.separator + "output");
		buildProps.put("config.dir",  configDir.getAbsolutePath());
		buildProps.put("dita.temp.dir", targetDir.getAbsolutePath()+ File.separator + "temp");
		buildProps.put("args.input", targetFile.getAbsolutePath());
		buildProps.put("args.output", resultFile.getName());
		buildProps.put("static.mapping", "yes");
		OutputStream fs = null;
		try {
			fs = new FileOutputStream(targetDir.getAbsolutePath()+ File.separator +"dita.build.properties");
			buildProps.store(fs, "Properties File for Trisoft Dynamic Publishing ANT job");
		} catch (IOException e) {
			CoreConstants.logger.severe("IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(fs != null ) try { fs.close(); } catch(Exception e) {}
		}
	}
	
//	@Override
	public void runStaticAnt(File targetDir, long id, String lang, String title, File uploadedFile) throws DynException {
		if(!targetDir.exists() || targetDir.list().length == 0) {
			CoreConstants.logger.severe(targetDir + " does not exist or is empty");
			throw new DynException(targetDir + " does not exist or is empty");
		}
		File targetFile = new File(targetDir + File.separator + uploadedFile);
		String line;
		Process p;
		BufferedReader br = null;
		StringBuilder buf = new StringBuilder();	
		String[] args = new String[] {CoreConstants.appPropsMap.get("ANT_UTIL"), "-noclasspath", 
				"-lib", CoreConstants.appPropsMap.get("ANT_LIBS_STATIC"), 
				"-logfile", CoreConstants.appPropsMap.get("ANT_LOG") + "_" + Long.toString(id) + ".txt",
				"-f", CoreConstants.appPropsMap.get("BUILD_XML_DEMO"),
				"-Dcustomization.dir=" + CoreConstants.appPropsMap.get("DITA_CUSTOM_DIR"),
				"\"-Daxf.path=" + CoreConstants.appPropsMap.get("AXF_PATH") + "\"",
				"-Dfo.ah.program.name=" + CoreConstants.appPropsMap.get("AH_PROG_NAME"),
				"-Doutput.dir=output",
				"-Ddocument.locale=" + lang,
				"-Dinput.dir=" + targetDir,
				"-DoutputFile=" + targetFile,
				"-Dbasedir=" + targetDir,
				"-Ddita.map.filename.root=" + id + "_" + title,
		"transform.add.publegent"};
		String cmd = "";
		for(String s: args )
			cmd = cmd.concat(s + " ");
		CoreConstants.logger.info(cmd);
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		try {
			p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while( (line = br.readLine()) != null)
				buf.append(line);
			int exitCode = p.waitFor();
			if(exitCode != 0) {
				CoreConstants.logger.severe(cmd + "\nfailed with exit code " + exitCode + "\nError log:\n" + buf.toString());
				throw new DynException(cmd + "\nfailed with exit code " + exitCode);
			}
		} catch (IOException e) {
			CoreConstants.logger.severe("PublisherImpl.runAnt(..): IOException: " + e.getMessage());
			throw new DynException("PublisherImpl.runAnt(..): IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			CoreConstants.logger.severe("PublisherImpl.runAnt(..): InterruptedException: " + e.getMessage());
			throw new DynException("PublisherImpl.runAnt(..): InterruptedException: " + e.getMessage());
		} finally {
			if(br != null) try { br.close(); } catch(Exception e) {}  
		}

	}

//	@Override
	public File targetDir(long id, String pubName) { 
		File targetDir = new File(CoreConstants.appPropsMap.get("OUTPUT_DIR"), pubName + "_" + id);
		return targetDir;
	}

	public File failedJobsDir(long id, String pubName) { 
		File dir = new File(CoreConstants.appPropsMap.get("FAILED_JOBS_DIR"), pubName + "_" + id);
		return dir;
	}

//	@Override
	public void runAnt(File targetDir, long id) throws DynException {
		if(!targetDir.exists() || targetDir.list().length == 0) {
			CoreConstants.logger.severe(targetDir + " does not exist or is empty");
			throw new DynException(targetDir + " does not exist or is empty");
		}
		if(!new File(targetDir + File.separator + "dita.build.properties").exists())
			throw new DynException(targetDir + ": missing dita.build.properties");		
		String line;
		Process p;
		BufferedReader br = null;
		StringBuilder buf = new StringBuilder();	
		String[] args = new String[] {CoreConstants.appPropsMap.get("ANT_UTIL"), "-noclasspath", 
				"-lib", '"' + CoreConstants.appPropsMap.get("ANT_LIBS") + '"', 
				"-logfile", CoreConstants.appPropsMap.get("ANT_LOG") + "_" + Long.toString(id) + ".txt",
				"-f", CoreConstants.appPropsMap.get("BUILD_XML"),
				"-propertyfile", targetDir.getPath() + File.separator + "dita.build.properties"};
		String cmd = "";
		for(String s: args )
			cmd = cmd.concat(s + " ");
		CoreConstants.logger.info(cmd);
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		try {
			p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while( (line = br.readLine()) != null)
				buf.append(line);
			int exitCode = p.waitFor();
			if(exitCode != 0) {
				CoreConstants.logger.severe(cmd + "\nfailed with exit code " + exitCode + "\nError log:\n" + buf.toString());
				throw new DynException(cmd + "\nfailed with exit code " + exitCode);
			}
		} catch (IOException e) {
			CoreConstants.logger.severe("PublisherImpl.runAnt(..): IOException: " + e.getMessage());
			throw new DynException("PublisherImpl.runAnt(..): IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			CoreConstants.logger.severe("PublisherImpl.runAnt(..): InterruptedException: " + e.getMessage());
			throw new DynException("PublisherImpl.runAnt(..): InterruptedException: " + e.getMessage());
		} finally {
			if(br != null) try { br.close(); } catch(Exception e) {}  
		}
	}

	private void createDirToProcess(File targetDir, File srcDir, File configDir, File profilesXML, DynamicPublishingPackage pack, Map<String,String> legend, long id, String lang) throws IOException {
		boolean success = targetDir.mkdir();
		if (success) {
			try {
				ToolKit.copyDirectory(srcDir, targetDir);
				CoreConstants.logger.info("Target Directory created: " + targetDir);
				createProperty(targetDir, configDir, pack, lang);
				CoreConstants.logger.info("Build properties created: " + targetDir.getAbsolutePath()+ File.separator +"dita.build.properties");
				createLegend(targetDir, profilesXML, legend, id, lang);
				CoreConstants.logger.info("Legend created: " + targetDir.getAbsolutePath()+ File.separator +"pubLegend.xml");
				createDitaval(targetDir, pack);
				CoreConstants.logger.info("Ditaval created: " + targetDir + File.separator + "dita.filter.ditaval");
			} catch (IOException e) {
				CoreConstants.logger.severe("IOException: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void createProperty(File targetDir, File configDir, DynamicPublishingPackage pack, String lang) {
		Properties buildProps = convertResourceBundleToProperties(ResourceBundle.getBundle(CoreConstants.TEMP_BUILD));
		String transtype = ("pdf2".equals(pack.outputType.toString()))? CoreConstants.appPropsMap.get("PDF_OUTPUT_TRANSTYPE"): pack.outputType.toString();
		buildProps.put("transtype", transtype);
		buildProps.put("basedir", targetDir.getAbsolutePath());
		buildProps.put("input.dir", targetDir.getAbsolutePath());
		buildProps.put("output.dir", targetDir.getAbsolutePath() + File.separator + "output");
		buildProps.put("config.dir",  configDir.getAbsolutePath());
		buildProps.put("dita.temp.dir", targetDir.getAbsolutePath()+ File.separator + "temp");
		//"generic" is different now
		/*
		if ("generic".equals(pack.pubPackage))
			buildProps.put("args.input", targetDir.getAbsolutePath() + File.separator + "dummy_bookmap.ditamap");
		else */
		buildProps.put("args.input", targetDir.getAbsolutePath() + File.separator + pack.ditaMaps[0].file + ".ditamap");
		buildProps.put("dita.input.valfile", targetDir.getAbsolutePath() + File.separator + "dita.filter.ditaval");
		OutputStream fs = null;
		try {
			fs = new FileOutputStream(targetDir.getAbsolutePath()+ File.separator +"dita.build.properties");
			buildProps.store(fs, "Properties File for Trisoft Dynamic Publishing ANT job");
		} catch (IOException e) {
			CoreConstants.logger.severe("IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(fs != null ) try { fs.close(); } catch(Exception e) {}
		}
	}

	private static void createLegend(File targetDir, File profilesXML, Map<String,String> legend, long id, String lang) {
		Locale loc = LocaleUtils.toLocale(lang);
		ResourceBundle bundle = ResourceBundle.getBundle("appStr", loc);
		DateFormat date = DateFormat.getDateInstance(DateFormat.LONG, loc);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setNamespaceAware(true);
		Document doc = null;
		Element el = null;
		Document xmldoc= new DocumentImpl();
		Element root = xmldoc.createElement("pubLegend");
		root.setAttribute("xml:lang", lang);
		root.setAttribute("desc", "The Legend info for the Dynamic Publishing PDF document cover page");
		for(Entry<String, String> entry: legend.entrySet()) {
			el = xmldoc.createElementNS(null, "legendEntry");
			el.setAttributeNS(null, "entryName", entry.getKey());
			el.setAttributeNS(null, "entryValue", entry.getValue());
			root.appendChild(el);
		}
		//DocID
		el = xmldoc.createElementNS(null, "legendEntry");
		el.setAttributeNS(null, "entryName", "docIDname");
		el.setAttributeNS(null, "entryValue", bundle.getString("main.docID"));
		root.appendChild(el);

		el = xmldoc.createElementNS(null, "legendEntry");
		el.setAttributeNS(null, "entryName", "docID");
		el.setAttributeNS(null, "entryValue", Long.toString(id));
		root.appendChild(el);

		//DocDate
		el = xmldoc.createElementNS(null, "legendEntry");
		el.setAttributeNS(null, "entryName", "docDate");
		el.setAttributeNS(null, "entryValue", date.format(new Date()));
		root.appendChild(el);

		File file= new File(targetDir.getAbsolutePath()+ File.separator +"pubLegend.xml");
		FileOutputStream fos = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.parse(profilesXML);
			Node node = doc.getDocumentElement().getElementsByTagName("coverStrings").item(0);
			node = xmldoc.importNode(node,true);
			root.appendChild(node);
			xmldoc.appendChild(root);
			fos = new FileOutputStream(file);
			OutputFormat of = new OutputFormat("XML","UTF-8",true);
			of.setIndent(1);
			of.setIndenting(true);
			of.setDoctype(null,CoreConstants.appPropsMap.get("DTD_DIR" + File.separator + "pub_legend.dtd"));
			XMLSerializer serializer = new XMLSerializer(fos,of);
			serializer.asDOMSerializer();
			serializer.serialize( xmldoc.getDocumentElement() );
		} catch (IOException e) {
			CoreConstants.logger.severe("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (DOMException e) {
			CoreConstants.logger.severe("DOMException: " + e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			CoreConstants.logger.severe("ParserConfigurationException: " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			CoreConstants.logger.severe("SAXException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(fos !=  null) try { fos.close();} catch(Exception e) {}
		}
	}

	private static Properties convertResourceBundleToProperties(ResourceBundle resource) {
		Properties properties = new Properties();
		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}
		return properties;
	}

	private static void createDitaval(File targetDir,
			DynamicPublishingPackage pack) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(targetDir + File.separator + "dita.filter.ditaval");
			fw.write("<?xml version=\"1.0\"?>\n");
			fw.write("<val>\n");
			for (Profile p : pack.profiles) {
				fw.write("\t<prop att=\"" + p.id + "\" action=\"exclude\"/>\n");
				for (ProfileValue v : p.values) {
					fw.write("\t<prop att=\"" + p.id + "\" val=\"" + v.id + "\" action=\"include\"/>\n");
					// TODO: This is customization we do not want!!!!!!!!!
					if ("dmp".equals(v.id))
						fw.write("\t<prop att=\"" + p.id + "\" val=\"DMP\" action=\"include\"/>\n");
				}
			}
			fw.write("</val>\n");
			fw.flush();
		} finally {
			if (fw != null)	fw.close();
		}
	}

	private static void createResult(File targetDir, File failedJobsDir, DynamicPublishingPackage.OUTPUT_TYPE outputType, long id, String resultName) throws DynException, IOException {
		CoreConstants.logger.info("Creating Result for output type " + outputType);
		File result = null;
		switch(outputType) {
		case pdf2: {
			File src = null;
			File renditionDir = new File(targetDir + File.separator + "output");
			File[] files = renditionDir.listFiles();
			if(!renditionDir.exists() || files == null || files.length == 0) {
				CoreConstants.logger.severe(renditionDir + " does not exist or empty");
				ToolKit.copyDirectory(targetDir, failedJobsDir);
				throw new DynException(renditionDir + " does not exist or empty");
			}
			for(File f: files) {
				if(!f.getName().endsWith(".pdf") || "pubLegend.pdf".equals(f.getName()))
					continue;
				src = f;
				break;
			}
			if(src == null) {
				CoreConstants.logger.severe("pdf file not found in " + targetDir);
				ToolKit.copyDirectory(targetDir, failedJobsDir);
				throw new DynException("pdf file not found in " + targetDir);
			}
			result = new File(CoreConstants.appPropsMap.get("RESULT_DIR") + File.separator + id + "_" + resultName + ".pdf");
			CoreConstants.logger.info("Copying pdf to DynPackRenditions folder");
			ToolKit.copyDirectory(src, result);
			CoreConstants.logger.info("Done Copying. Ready to store in the database.");
			break;
		}
		case xhtml: {
			result = new File(CoreConstants.appPropsMap.get("DYN_PUB_HOME_DIR") + File.separator + "dyn_pub_outputs" + File.separator + "Results" + File.separator + id + "_DynPubOutput.zip");
			ToolKit.zipDirectory(new File(targetDir.getAbsoluteFile() + File.separator + "output"), result);
			break;
		}
		default:
			throw new DynException("Unsupported output type: " + outputType.name());
		}
	}

}
