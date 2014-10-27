package trisoftdp.ejb;

import javax.ejb.Remote;

@Remote
public interface ProcessingBeanRemote {

	String sayHalloRemote(String name);
}
