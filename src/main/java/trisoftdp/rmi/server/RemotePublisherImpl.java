package trisoftdp.rmi.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;

public class RemotePublisherImpl implements RemotePublisher {

	//Remote Cache Service is injected...
	//ICacheService cacheService;
	

	public void process(long id, String configId, String contentDir, String configDir, DynamicPublishingPackage pack, Map<String, String> legend, String lang) throws DynException, IOException {
		// TODO Auto-generated method stub
	}

	public long process(MementoUserBean user, ProdEnvBean prodEnv, String lang)	throws DynException, IOException {
		// TODO Auto-generated method stub
		System.out.println("long process(MementoUserBean user, ProdEnvBean prodEnv, String lang) called. lang=" + lang);
		return 0;
	}

	public void processStatic(long id, MementoUserBean user, ProdEnvBean prodEnv, String lang, File uploadedFile) throws DynException, IOException {
		// TODO Auto-generated method stub		
	}

	public File targetDir(long id, String configId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void runAnt(File targetDir, long id) throws DynException {
		// TODO Auto-generated method stub
	}

	public void runStaticAnt(File targetDir, long id, String lang, String title, File uploadedFile) throws DynException {
		// TODO Auto-generated method stu
	}

	
}
