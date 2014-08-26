package trisoftdp.core;

import java.io.File;
import java.util.Map;

public class DynPubNotifications {
	public static String getCompleteMessage(long id, DynamicPublishingPackage pack, Map<String,String> appStringsMap, ProdEnvBean prodEnv) {
		String nameOfPdf = pack.ditaMaps[0].title;
		File f = new File(prodEnv.getProductDir());
		String prod = f.getName();
		String script = "";
		script += "<p>" + appStringsMap.get("main.delivery.complete") + "</p>\n";
		script += "<p>" + appStringsMap.get("main.delivery.viewLink") + "</p>\n";
		script += "<p><a href='http://" + (CoreConstants.HOST + "/" + CoreConstants.appPropsMap.get("APP_DIR") + "/Result?docId=" + id + "' rel='" + prod + "'>" + nameOfPdf + ".pdf").replace("//", "/") + "</a></p>\n";
		script += "<p>" + appStringsMap.get("main.delivery.id") + " <b>" + id + "</b>.</p>\n";
		
		//below lines commented by Chaya Somanchi for TDP 1.1b requirement 5.1.3.Improve existing user feedback visibility
		/*script += "<p>" + appStringsMap.get("main.delivery.forQuestions");
		script += " <a href='mailto:" + emailFrom + "'>" + emailFrom + "</a><br/>";
		script += appStringsMap.get("main.delivery.refId") + " <b>" + id + "</b>.</p>\n";*/

		return script;
	}
	
	public static String getUserPackDetails(String userEmail, UserBean user, Map<String,String> appStringsMap) {
		String script = "";
		script += "<div style = 'font-family: Verdana; font-size: 12px; line-height: 16px;'>\n";
		if (userEmail != null)
			script += "<p><b>" + appStringsMap.get("feedback.email.userMail") + "</b>&nbsp;&nbsp;&nbsp;&nbsp;" + userEmail + "</p>\n";
		script += "<p><b>" + appStringsMap.get("feedback.email.package") + "</b>&nbsp;&nbsp;&nbsp;&nbsp;" + user.getUserPack().packageName + "<br/>\n";
		script += "<p><b>" + appStringsMap.get("feedback.email.publication") + "</b>&nbsp;&nbsp;&nbsp;&nbsp;" + user.getUserPack().ditaMaps[0].task + "</p>\n";
		script += "<p><b>" + appStringsMap.get("feedback.email.conditions") + "</b><br/>\n";
		for (DynamicPublishingPackage.Profile profile: user.getUserPack().profiles) {
			script += "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>" + 
					user.getProfileAliases().get(profile.name) + ": </b>&nbsp;&nbsp;&nbsp;&nbsp;";
			for (DynamicPublishingPackage.ProfileValue value: profile.values) {
				script += user.getProfileAliases().get(value.id) + "; ";
			}
			script += "<br/>\n";
		}
		script += "</p></div>\n";
		
		return script;
	}

}
