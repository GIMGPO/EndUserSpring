package trisoftdp.core;

import java.io.File;
import java.io.Serializable;

public class ProdEnvBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String productDir;
	private String prodConfigDir;
	private String prodDpPackDir;
	private String prodContentDir;
	private String prodRelDocsDir;
	private String prodRelDocsCacheDir;
	private String prodResultDir;
	private String prodSupportEmail;
	private String prodCleanAfter;
	private String prodShowRelease;
	private String prodL10NSupport;
	private String prodResourceBundle; //Strings specific to the product

	public ProdEnvBean() {}
	
	public ProdEnvBean(String productGroupEnv, String bl) throws DynException { 
		String productDir = CoreConstants.appPropsMap.get("MAIN_DIR") + File.separator + productGroupEnv + File.separator;
		setProductDir(productDir);
		setProdConfigDir(productDir + CoreConstants.appPropsMap.get("PRODUCT_CONFIG_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator);
		setProdDpPackDir(productDir + CoreConstants.appPropsMap.get("PRODUCT_DYN_PACK_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator);
		setProdContentDir(productDir + CoreConstants.appPropsMap.get("PRODUCT_SOURCE_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator);
		setProdRelDocsDir(productDir + CoreConstants.appPropsMap.get("PRODUCT_RELDOCS_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator);
		setProdResultDir(CoreConstants.appPropsMap.get("RESULT_DIR") + File.separator);
		setProdRelDocsCacheDir(productDir + CoreConstants.appPropsMap.get("PRODUCT_RELDOCSCACHE_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator);
		setProdL10NSupport(ProductGroupData.getProductProperties(productGroupEnv).getProperty("LOCALIZATION_SUPPORT"));
		setProdCleanAfter(ProductGroupData.getProductProperties(productGroupEnv).getProperty("CLEAN_AFTER"));
		setProdShowRelease(ProductGroupData.getProductProperties(productGroupEnv).getProperty("SHOW_RELEASE"));
		setProdSupportEmail(ProductGroupData.getProductProperties(productGroupEnv).getProperty("EMAIL_FROM"));
	}
	
	public String getProductDir() { return productDir; }
	public void setProductDir(String productDir) { this.productDir = productDir; }

	public String getProdConfigDir() { return prodConfigDir; }
	public void setProdConfigDir(String prodConfigDir) { this.prodConfigDir = prodConfigDir; }

	public String getProdDpPackDir() { return prodDpPackDir; }
	public void setProdDpPackDir(String prodDpPackDir) { this.prodDpPackDir = prodDpPackDir; }
	
	public String getProdContentDir() { return prodContentDir; }
	public void setProdContentDir(String prodContentDir) { this.prodContentDir = prodContentDir; }

	public String getProdRelDocsDir() { return prodRelDocsDir; }
	public void setProdRelDocsDir(String prodRelDocsDir) { this.prodRelDocsDir = prodRelDocsDir; }
	
	public String getProdResultDir() { return prodResultDir; }
	public void setProdResultDir(String prodResultDir) { this.prodResultDir = prodResultDir; }
	
	public String getProdRelDocsCacheDir() { return prodRelDocsCacheDir; }
	public void setProdRelDocsCacheDir(String prodRelDocsCacheDir) { this.prodRelDocsCacheDir = prodRelDocsCacheDir; }
	
	public String getProdSupportEmail() { return prodSupportEmail; }
	public void setProdSupportEmail(String prodSupportEmail) { this.prodSupportEmail = prodSupportEmail; }
	
	public String getProdCleanAfter() { return prodCleanAfter; }
	public void setProdCleanAfter(String prodCleanAfter) { this.prodCleanAfter = prodCleanAfter; }
	
	public String getProdShowRelease() { return prodShowRelease; }
	public void setProdShowRelease(String prodShowRelease) { this.prodShowRelease = prodShowRelease; }
	
	public String getProdL10NSupport() { return prodL10NSupport; }
	public void setProdL10NSupport(String prodL10NSupport) { this.prodL10NSupport = prodL10NSupport; }
	
	public String getProdResourceBundle() { return prodResourceBundle; }
	public void setProdResourceBundle(String prodResourceBundle) { this.prodResourceBundle = prodResourceBundle; }
	
	public void clean() {
		productDir = null;
		prodConfigDir = null;
		prodDpPackDir = null;
		prodContentDir = null;
		prodRelDocsDir = null;
		prodResultDir = null;
		prodRelDocsCacheDir = null;
		prodSupportEmail = null;
		prodCleanAfter = null;
		prodShowRelease = null;
		prodL10NSupport = null;
		prodResourceBundle = null;
	}

}
