package trisoftdp.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 * Session Bean implementation class ProcessingBean
 */
@Singleton
@LocalBean
public class ProcessingBean implements ProcessingRemote {

    /**
     * Default constructor. 
     */
    public ProcessingBean() {
        // TODO Auto-generated constructor stub
    }
    
    public String sayHalloRemote(String name) {
    	return "Hallo " + name;
    }

}
