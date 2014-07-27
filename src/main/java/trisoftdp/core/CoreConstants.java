package trisoftdp.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.lang3.LocaleUtils;



public class CoreConstants {
	private static enum OPERATION_MODE {PRODUCTION, DEBUG, TEST, LOCAL};
	private static OPERATION_MODE operationMode = OPERATION_MODE.PRODUCTION;
	public static enum REQUEST_PARAM {configId, docId, doProfiles, helpFolder, helpFile, lang, outFormat, outputType, page, prod, state, previous, file, xmlFile, zipFile, userEmail };
	public static final String EOL;
	public static final String TEMP_BUILD = "templateBuild";
	public static final boolean DEBUG = true;
	public static String SMTP_HOST = "mail.smtp.host";
	public static String MAIL_HUB_HOST = "mailsyshubprd05.lss.emc.com";
	public static String HOST;
	public static final Map<String,String> appPropsMap = new HashMap<String,String>();
	public static final Map<String,String> appStringsMap = new HashMap<String,String>();
	public static final Map<String,String> tempBuildProps = new HashMap<String,String>();
	public static final Map<String,String> languagesMap = new HashMap<String,String>();
	public static final Locale[] dynPubLocales;
	public static final Logger logger;
	//public static String dbUrl = "jdbc:sqlserver://edpappprd10:1433;databaseName=TrisoftDP;userName=sa;password=TechPub2005;";
	//public static String dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String dbUrl = "jdbc:mysql://" + appPropsMap.get("MYSQL_DB_HOST") + ":3306/" + appPropsMap.get("MYSQL_DB_NAME");
	public static String dbDriver = "com.mysql.jdbc.Driver";
	//DynPubThreadPool parameters:
	public static final int maxAvailable = 2;  //maximal number of jobs permitted by the semaphore 
	public static final int corePoolSize = 10; //the number of threads to keep in the pool, even if they are idle
	public static final int maximumPoolSize = Integer.MAX_VALUE; //the maximum number of threads to allow in the pool. If set to Integer.MAX_VALUE then the pool is effectively non-blocking 
	public static final long keepAliveTime = 2; //when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating (in hours)
	public static final int sleepTime = 1000; //sleep time before checking for notifications (in milliseconds) 
	public static final long period = 4/*hour*/*60/*min*/*60/*sec*/*1000; //period of checking for left over fixes (in milliseconds)
	public static final TimeUnit unit = TimeUnit.HOURS;

	static {

		EOL = System.getProperty("line.separator");
//		populateMap(appPropsMap, ResourceBundle.getBundle("mac_appCore"));
		populateMap(appPropsMap, ResourceBundle.getBundle("appCore"));		
		populateMap(appStringsMap, ResourceBundle.getBundle("appStr"));
		populateMap(tempBuildProps, ResourceBundle.getBundle(TEMP_BUILD));
		populateMap(languagesMap, ResourceBundle.getBundle("languages"));
		dynPubLocales = new Locale[languagesMap.size()];
		int idx = 0;
		for(String key: languagesMap.keySet())
			dynPubLocales[idx++]= LocaleUtils.toLocale(key);
		logger = Logger.getLogger("endUser");
		try {
			//HOST = java.net.InetAddress.getLocalHost().getCanonicalHostName();
			HOST = appPropsMap.get("APP_HOST");
			if (!"80".equals(appPropsMap.get("TOMCAT_PORT")))
				HOST = HOST + ":" + appPropsMap.get("TOMCAT_PORT");
			FileHandler handler = new FileHandler(appPropsMap.get("END_USER_LOG_FILE"));
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			if(DEBUG)
				logger.setLevel(Level.ALL);
			else
				logger.setLevel(Level.WARNING);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final void populateMap(Map<String,String> map, ResourceBundle bundle) {
		String key;
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			key = keys.nextElement();
			map.put(key, bundle.getString(key));
		}	
	}
}


