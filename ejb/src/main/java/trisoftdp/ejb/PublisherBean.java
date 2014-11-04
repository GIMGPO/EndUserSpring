package trisoftdp.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.UserBean;
import trisoftdp.processing.PublisherImpl;

@Singleton
@LocalBean
public class PublisherBean  implements PublisherRemote{
	static private final PublisherImpl publisher = new PublisherImpl();
	@Override
	public void process(long id, String configId, String contentDir,
			String configDir, DynamicPublishingPackage pack,Map<String, String> legend, String lang) throws DynException, IOException {
		System.out.println("process(long id, String configId, String contentDir,String configDir, DynamicPublishingPackage pack,Map<String, String> legend, String lang) called");
		//publisher.process(id, configId, contentDir, configDir, pack, legend, lang);
	}

	@Override
	public long process(UserBean user, ProdEnvBean prodEnv, String lang) throws DynException, IOException {
		System.out.println("process(UserBean user, ProdEnvBean prodEnv, String lang) called");
		//return publisher.process(user, prodEnv, lang);
		return 0;
	}

	@Override
	public void processStatic(long id, UserBean user, ProdEnvBean prodEnv, String lang, File uploadedFile, boolean cleanup) throws DynException, IOException {
		System.out.println("processStatic(...) called");
		//publisher.processStatic(id, user, prodEnv, lang, uploadedFile, cleanup);
	}

}
