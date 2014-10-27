package trisoftdp.ejb;

import javax.ejb.Local;

@Local
public interface ProcessingBeanLocal {

	String sayHalloLocal(String name);
}
