package trisoftdp.web.ejb.client;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynException;
import trisoftdp.ejb.PublisherBean;
import trisoftdp.ejb.PublisherRemote;
import trisoftdp.processing.Publisher;

public class EJBPublisher {

	private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
	private static Context initialContext = null;
	private static volatile PublisherRemote publisher = null; 
	
	public static void main(String[] args) {
		test();
	}
	
	private EJBPublisher() {}
	
	public static Publisher getThePublisher() {
		if(publisher == null) {
	        synchronized(EJBPublisher.class) {
	        	try { 	        
	        		if (initialContext == null) {
	        			Properties properties = new Properties();
	        			properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);          
	        			initialContext = new InitialContext(properties);
	        		} 
	        		String lookupName = String.format(CoreConstants.EJB_LOOKUP_FORMAT, PublisherBean.class.getSimpleName(),PublisherRemote.class.getName());
	        		publisher = (PublisherRemote) initialContext.lookup(lookupName); 
	        	} catch (NamingException e) {
	        		//TODO
	        		e.printStackTrace();
	        	}
	        }
		}
		return publisher;
	}
	
	@Deprecated
	public static void test() { 
		PublisherRemote publisher = doLookupPublisher();
        try {
			publisher.process(null, null, "En");
		} catch (DynException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
    public static Context getInitialContext() throws NamingException {
        if (initialContext == null) {
            Properties properties = new Properties();
            properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);          
            initialContext = new InitialContext(properties);
        }        
        return initialContext;
    }
    
    @Deprecated
    private static PublisherRemote doLookupPublisher() {
        Context context = null;
        PublisherRemote publisher = null;
        try { 
            context = getInitialContext();
            String lookupName = String.format("ejb:EndUserSpring-ear/EndUserSpring-ejb//%s!%s", PublisherBean.class.getSimpleName(),PublisherRemote.class.getName());
            System.out.println("lookupName=" + lookupName);
            publisher = (PublisherRemote) context.lookup(lookupName); 
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return publisher;
    }
    
}
