package trisoftdp.rmi.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;

public interface RemotePublisher {
	
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
	public void process(long id, String configId, String contentDir, String configDir, DynamicPublishingPackage pack, Map<String,String> legend, String lang) throws DynException, IOException;
	
	/**
	 * 
	 * @param user
	 * @param prodEnv
	 * @param lang
	 * @return
	 * @throws DynException
	 * @throws IOException
	 */
	public  long process(MementoUserBean user, ProdEnvBean prodEnv, String lang) throws DynException, IOException;

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
	public void processStatic(long id, MementoUserBean user, ProdEnvBean prodEnv, String lang, File uploadedFile) throws DynException, IOException;
	
	/**
	 * 
	 * @param id
	 * @param configId
	 * @return
	 */
	public  File targetDir(long id, String configId);

	/**
	 * 
	 * @param targetDir
	 * @param id
	 * @throws DynException
	 */
	public void runAnt(File targetDir, long id) throws DynException ;

	/**
	 * 
	 * @param targetDir
	 * @param id
	 * @param lang
	 * @param title
	 * @param uploadedFile
	 * @throws DynException
	 */
	public void runStaticAnt(File targetDir, long id, String lang, String title, File uploadedFile) throws DynException;


}
