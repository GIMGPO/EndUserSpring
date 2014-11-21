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
		if (userEmail != null)
			script += "<tr><td><b>" + appStringsMap.get("feedback.email.userMail") + "</b></td><td>" + userEmail + "</td></tr>\n";
		else 
			script += "<tr><td><b>" + appStringsMap.get("feedback.email.userMail") + "</b></td><td>Not provided</td></tr>\n";
		script += "<tr><td><b>" + appStringsMap.get("feedback.email.package") + "</b></td><td>" + user.getUserPack().packageName + "</td></tr>\n";
		script += "<tr><td><b>" + appStringsMap.get("feedback.email.publication") + "</b></td><td>" + user.getUserPack().ditaMaps[0].task + "</td></tr>\n";
		script += "<tr><td style='vertical-align:top'><b>" + appStringsMap.get("feedback.email.conditions") + "</b></td><td>\n";
		for (DynamicPublishingPackage.Profile profile: user.getUserPack().profiles) {
			String profilename = (user.getProfileAliases().get(profile.name)!=null)? user.getProfileAliases().get(profile.name):profile.name;
			script += "<b>" + 
					profilename + ": </b>";
			for (DynamicPublishingPackage.ProfileValue value: profile.values) {
				String profilevalue = (user.getProfileAliases().get(value.id)!=null)? user.getProfileAliases().get(value.id):value.id;
				script += profilevalue + "; ";
			}
			script += "<br/>\n";
		}
		script += "</td></tr>\n";
		
		return script;
	}

}
