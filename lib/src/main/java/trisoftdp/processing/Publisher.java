package trisoftdp.processing;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.UserBean;
import trisoftdp.core.ProdEnvBean;

/**
 * 
 * @author shadrn1
 *
 */
public interface Publisher {
	/**
	 * 
	 * @param id
	 * @param configId
	 * @param contentDir
	 * @param configDir
	 * @param pack
	 * @param legend
	 * @param lang
	 * @throws DynException
	 * @throws IOException
	 */
	public void process(long id, String configId, String contentDir, String configDir, DynamicPublishingPackage pack, Map<String,String> legend, String lang) throws DynException, IOException;	/**

	/**
	 * 
	 * @param user
	 * @param prodEnv
	 * @param lang
	 * @return
	 * @throws DynException
	 * @throws IOException
	 */
	public  long process(UserBean user, ProdEnvBean prodEnv, String lang) throws DynException, IOException;

	/**
	 * 
	 * @param id
	 * @param configId
	 * @param pack
	 * @param legend
	 * @param lang
	 * @param uploadedFile
	 * @throws DynException
	 * @throws IOException
	 */
	public void processStatic(long id, UserBean user, ProdEnvBean prodEnv, String lang, String uploadedFilePath, boolean cleanup) throws DynException, IOException;


}
