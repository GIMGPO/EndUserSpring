package trisoftdp.core;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class UserBean implements HttpSessionBindingListener, Serializable {
	private static final long serialVersionUID = 1L;
	private String userEmail;
	private String configId;
	private Map<String,String> profileAliases;
	private Map<String,String> pubLegend;
	private DynamicPublishingPackage dynPack;
	private DynamicPublishingPackage userPack;

	public UserBean() {}


	public boolean getProfIsSet() { 
		boolean isSet = false;
		if(	userPack.profiles.length > 0 
		&&	userPack.profiles[0].name != null
		&&  userPack.profiles[0].values[0] != null
		&&  userPack.profiles[0].id != null)
		isSet = true;
		return isSet;
	}
	
	public String getConfigId() { return configId; }
	public void setConfigId(String configId) { this.configId = configId; }

	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

	public Map<String,String> getProfileAliases() { return profileAliases; }
	public void setProfileAliases(Map<String,String> profileAliases) { this.profileAliases = profileAliases; }

	public Map<String,String> getPubLegend() { return pubLegend; }
	public void setPubLegend(Map<String,String> pubLegend) { this.pubLegend = pubLegend; }

	public DynamicPublishingPackage getUserPack() { return userPack; }
	public void setUserPack(DynamicPublishingPackage userPack) { this.userPack = userPack; }

	public DynamicPublishingPackage getDynPack() { return dynPack; }
	public void setDynPack(DynamicPublishingPackage dynPack) { this.dynPack = dynPack; }

	public void clean() {
		userEmail = null;
		configId = null;
		profileAliases = null;
		pubLegend = null;
		dynPack = null;
		userPack = null;
	}

//	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
	}

//	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		//System.out.println("UserBean.valueUnbound() called");	
	}

}
