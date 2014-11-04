package trisoftdp.web.ejb.client;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import trisoftdp.core.DynException;
import trisoftdp.ejb.PublisherBean;
import trisoftdp.ejb.PublisherRemote;

public class EJBPublisher {

	private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
	private static Context initialContext = null;
	
	public static void main(String[] args) {
		test();
	}
	
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
	
    public static Context getInitialContext() throws NamingException {
        if (initialContext == null) {
            Properties properties = new Properties();
            properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);          
            initialContext = new InitialContext(properties);
        }        
        return initialContext;
    }
    
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
