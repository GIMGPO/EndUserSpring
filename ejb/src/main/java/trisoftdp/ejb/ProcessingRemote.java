package trisoftdp.ejb;

import javax.ejb.Remote;

@Remote
public interface ProcessingRemote {

	String sayHalloRemote(String name);
}
