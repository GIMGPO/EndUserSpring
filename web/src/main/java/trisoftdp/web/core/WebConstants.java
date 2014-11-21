package trisoftdp.web.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import trisoftdp.core.CoreConstants;

public class WebConstants {
	public static final boolean DEBUG = true;
	public static final String EOL = System.getProperty("line.separator");
	public static final Logger logger;

	public static final int maxAvailable = 2;  //maximal number of jobs permitted by the semaphore 
	public static final int corePoolSize = 10; //the number of threads to keep in the pool, even if they are idle
	public static final int maximumPoolSize = Integer.MAX_VALUE; //the maximum number of threads to allow in the pool. If set to Integer.MAX_VALUE then the pool is effectively non-blocking 
	public static final long keepAliveTime = 2; //when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating (in hours)
	public static final int sleepTime = 1000; //sleep time before checking for notifications (in milliseconds) 
	public static final long period = 4/*hour*/*60/*min*/*60/*sec*/*1000; //period of checking for left over fixes (in milliseconds)
	public static final TimeUnit unit = TimeUnit.HOURS;
	public static final Map<String,String> webPropsMap = new HashMap<String,String>();

	static {
//		ResourceBundle webCoreBundle = ResourceBundle.getBundle("mac_webCore");
		ResourceBundle webCoreBundle = ResourceBundle.getBundle("webCore");		
		logger = Logger.getLogger("webEndUser");
		if(DEBUG)
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.WARNING);
		try {
			CoreConstants.populateMap(webPropsMap, ResourceBundle.getBundle("webCore_TEST_ROOT"));
			FileHandler handler = new FileHandler(webCoreBundle.getString("WEB_END_USER_LOG_FILE"));
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
