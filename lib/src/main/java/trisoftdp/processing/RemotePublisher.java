package trisoftdp.processing;

import java.io.IOException;
import java.util.Map;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;

public interface RemotePublisher {
	
	public void process(long id, String configId, String contentDir, String configDir, DynamicPublishingPackage pack, Map<String,String> legend, String lang) throws DynException, IOException;
	
	public  long process(MementoUserBean user, ProdEnvBean prodEnv, String lang) throws DynException, IOException;
	
	public void processStatic(long id, MementoUserBean user, ProdEnvBean prodEnv, String lang, byte[] uploadedFileData, boolean cleanup) throws DynException, IOException;
	
}
