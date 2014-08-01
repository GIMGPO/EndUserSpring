package trisoftdp.rmi.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.processing.Publisher;
import trisoftdp.processing.RemotePublisher;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;

public class RemotePublisherImpl implements RemotePublisher {

	Publisher localPublisher;
	
	//@Autowired
	public void setLocalPublisher(Publisher localPublisher) { 
		this.localPublisher = localPublisher;
	}

	public void process(long id, String configId, String contentDir, String configDir, DynamicPublishingPackage pack, Map<String, String> legend, String lang) throws DynException, IOException {
		localPublisher.process(id, configId, contentDir, configDir, pack, legend, lang);
		//System.out.format("id=%d configId=%s lang=%s%n", id, configId, lang);
	}

	public long process(MementoUserBean user, ProdEnvBean prodEnv, String lang)	throws DynException, IOException {
		return localPublisher.process(user, prodEnv, lang);
	}
	
	public void processStatic(long id, MementoUserBean user, ProdEnvBean prodEnv, String lang, byte[] uploadedFileData, boolean cleanup) throws DynException, IOException {
		// TODO do not forget to unzip if it is really a zip file
		File tmpFile = File.createTempFile("upload", ".zip");
		FileOutputStream fos = new FileOutputStream(tmpFile);
		fos.write(uploadedFileData);
		fos.flush();
		fos.close();
		localPublisher.processStatic(id, user, prodEnv, lang, tmpFile, cleanup);
		tmpFile.delete();
	}




}
