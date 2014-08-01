package servlets;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.LocaleUtils;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.CoreConstants.REQUEST_PARAM;
import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.PackageData;
import trisoftdp.core.ProdEnvBean;
import trisoftdp.core.ToolKit;
import trisoftdp.web.core.DbWebHelper;
import trisoftdp.web.core.WebMementoUserBean;

/**
 * Servlet implementation class DynDispatcher
 */
public class DynDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Map<String,Map<String,String>> flowMap;
	private static final Pattern malicious = Pattern.compile(".*[><'\"\n\r\\(\\)].*");
	
	static {
		//normal
		flowMap = new HashMap<String,Map<String,String>>();
		Map<String,String> map = new  HashMap<String,String>();
		map.put(null, "start");
		map.put("start", "displayBooks");
		map.put("displayBooks", "displayQuestions");
		map.put("displayQuestions", "displayDelivery");
		map.put("displayDelivery", "finish");
		flowMap.put("normal", map);
		//reverse
		map.put(null, "start");
		map = new  HashMap<String,String>();
		map.put("start", "displayQuestions");
		map.put("displayQuestions", "displayBooks");
		map.put("displayBooks", "displayDelivery");
		map.put("displayDelivery", "finish");
		flowMap.put("reverse", map);
		//default
		map = new  HashMap<String,String>();
		map.put(null, "start");
		map.put("reldocs", "start");
		map.put("admin", "start");
		flowMap.put("default", map);
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DynDispatcher() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute("lang", "en_US");
		String feedback = (String) session.getAttribute("feedback");
	    if (request.getCookies() != null) {
			for (Cookie c: request.getCookies())
				if ("appLang".equals(c.getName()))
					session.setAttribute("lang", c.getValue());
		}
	    String bl = (String) session.getAttribute("lang");
	    Map<String,String> appStringsMap = new HashMap<String,String>();
		Locale l = LocaleUtils.toLocale(bl);
		CoreConstants.populateMap(appStringsMap, ResourceBundle.getBundle("appStr", l));
		WebMementoUserBean user = null; 
		Matcher mtch;
		boolean goBack = request.getParameter(REQUEST_PARAM.previous.name()) != null;
		boolean goRelDocs = false;
		String flow = (session.getAttribute("flow")==null)? "default":(String) session.getAttribute("flow");
		if(!flowMap.containsKey(flow))
			throw new ServletException("Unexpected flow=" + flow);
		
		
		@SuppressWarnings("unchecked")
		Enumeration<String >pars = request.getParameterNames();
		while(pars.hasMoreElements()) {
			String param = pars.nextElement();
			System.out.format("%s=%s%n", param, request.getParameter(param));
		}
		
		synchronized (session) {                                                                                                                                      
			user = (WebMementoUserBean) session.getAttribute("user");      

	        if (user == null) {                                                                                                                                          
	          user = new WebMementoUserBean();                                                                                                              
	          session.setAttribute("user", user);                                                               
	        } 
	        if(user.getPubLegend() == null) 
				 user.setPubLegend(new HashMap<String,String>());
	        if(user.getDynPack() == null) 
				 user.setDynPack(new DynamicPublishingPackage());
	        
	        if (user.getUserPack() == null) {
				user.setUserPack(new DynamicPublishingPackage());
				user.getUserPack().pubPackage = user.getDynPack().pubPackage;
				user.getUserPack().packageName = user.getDynPack().packageName;
				user.getUserPack().productRelease = user.getDynPack().productRelease;
				user.getUserPack().createDate = user.getDynPack().createDate;
				user.getUserPack().ditaMaps = new DynamicPublishingPackage.DitaMap[1];
				user.getUserPack().ditaMaps[0] = new DynamicPublishingPackage.DitaMap();
				user.getUserPack().languages = new String[1];
				user.getUserPack().languages[0] = CoreConstants.appStringsMap.get("header.lang." + bl);
				user.getUserPack().profiles = new DynamicPublishingPackage.Profile[user.getDynPack().profiles.length];
				for (int i=0; i<user.getDynPack().profiles.length; i++) {
					user.getUserPack().profiles[i] = new DynamicPublishingPackage.Profile();
					user.getUserPack().profiles[i].status = new DynamicPublishingPackage.Profile().status;
					user.getUserPack().profiles[i].quesNum = user.getDynPack().profiles[i].quesNum;
					user.getUserPack().profiles[i].values = null;
				}
			
// Removed "generic" block
	        }
		}

		if(!goBack) { //save current info in stacks
			try {
				user.push(request);
				//Only user.getParameter should be used thereafter
			} catch (DynException e) {
				throw new ServletException(e.getMessage());
			}
		}
		else {
			try {
				if(!user.getIsStackEmpty())
					user.pop();
				else
					throw new ServletException("Stack is empty");
			} catch (DynException e) {
				throw new ServletException("DynException: " + e.getMessage());
			}
		}
		//this line should be after user.pop(request);
		boolean doProfiles = user.getParameter(REQUEST_PARAM.doProfiles.name()) != null;
	    
		ProdEnvBean prodEnv = (ProdEnvBean) session.getAttribute("prodEnv");                                                                                                                   
	    if (prodEnv == null) {                                                                                                                                          
	       prodEnv = new ProdEnvBean();                                                                                                              
	       session.setAttribute("prodEnv", prodEnv);                                                               
	    }                        
		for(REQUEST_PARAM param: REQUEST_PARAM.values()) {
			String value;
			if((value = user.getParameter(param.name())) == null)
				continue;
			switch(param) {
			//configId, docId, helpFolder, helpFile, lang, outFormat, outputType, page, prod, state, previous, file, xmlFile, zipFile, userEmail
			case lang:				
				// If the language is changed 
				cleanUp(session);
				synchronized(session) {
					String reqLang = user.getParameter("lang");
					if(reqLang != null && ToolKit.isLangSupported(reqLang)) {
						//if(session.getAttribute("state") == "start")
						//	session.removeAttribute("state");
						Cookie appLang = new Cookie("appLang", reqLang);
						appLang.setMaxAge(365*24*60*60);
						response.addCookie(appLang);
						session.setAttribute("lang", reqLang);
					}
					bl = (String) session.getAttribute("lang");
				} //end of synchronized	
				response.sendRedirect("index.jsp");
				return;
			case configId:
				mtch = malicious.matcher(value);
				
				if(mtch.matches())
					throw new ServletException("configId has malicious characters: " + value);
				user.setConfigId(value);
				File profAliasesXML = new File(prodEnv.getProdConfigDir() + "ProfileAliases.xml");
				File xmlFile = new File(prodEnv.getProdDpPackDir() + user.getConfigId() + ".xml");
				if (profAliasesXML.exists())
					try {
						user.setProfileAliases(PackageData.getProfileAliases(profAliasesXML));
					} catch (DynException e) {
						throw new ServletException("DynException: " + e.getMessage());
					}
				else
					throw new ServletException("file does not exist " + profAliasesXML);
				if (xmlFile.exists()) {
					try {
						user.setDynPack(PackageData.getPackage(xmlFile));
					} catch (DynException e) {throw new ServletException("DynException: " + e.getMessage());}
					
					DynamicPublishingPackage.Profile[] vp = PackageData.getVisibleProfiles(user.getDynPack());
					String[] autoSel = new String[vp.length];
					String[][] profileValue = new String[vp.length][];
					String len[] = new String[vp.length];
					for (int i=0; i<vp.length; i++) {
						autoSel[i] = vp[i].selectType.toString();
						len[i] = Integer.toString(vp[i].values.length);
						profileValue[i] = new String[vp[i].values.length];
						for (int j=0; j<vp[i].values.length; j++)
							profileValue[i][j] = user.getProfileAliases().containsKey(vp[i].values[j].id)? user.getProfileAliases().get(vp[i].values[j].id): vp[i].values[j].name;
					}
					session.setAttribute("visibleProfiles", vp);
					session.setAttribute("profileValueName", profileValue);
					session.setAttribute("autoSelect", autoSel);
					
					flow = ("no".equals(user.getDynPack().quesFirst.toString()))?  "normal":"reverse";
					session.setAttribute("flow",flow);		
					
					user.getUserPack().pubPackage = user.getDynPack().pubPackage;
					user.getUserPack().packageName = user.getDynPack().packageName;
					user.getUserPack().productRelease = user.getDynPack().productRelease;
					user.getUserPack().createDate = user.getDynPack().createDate;
					user.getUserPack().ditaMaps = new DynamicPublishingPackage.DitaMap[1];
					user.getUserPack().ditaMaps[0] = new DynamicPublishingPackage.DitaMap();
					user.getUserPack().languages = new String[1];
					user.getUserPack().languages[0] = appStringsMap.get("header.lang." + bl);
					user.getUserPack().profiles = new DynamicPublishingPackage.Profile[user.getDynPack().profiles.length];
					for (int i=0; i<user.getDynPack().profiles.length; i++) {
						user.getUserPack().profiles[i] = new DynamicPublishingPackage.Profile();
						user.getUserPack().profiles[i].status = new DynamicPublishingPackage.Profile().status;
						user.getUserPack().profiles[i].quesNum = user.getDynPack().profiles[i].quesNum;
						user.getUserPack().profiles[i].values = null;
					}
// Moved "generic" block here from the  if (user.getUserPack() == null) cluster
					if ("generic".equals(user.getUserPack().pubPackage)) {
						session.setAttribute("static","yes");
					}
				}
				else
					throw new ServletException("There is no documentation with this configuration: " + user.getConfigId());				
				break;
			case file: 
				mtch = malicious.matcher(value);
				if(mtch.matches())
					throw new ServletException("File name has malicious characters: " + value);				
				for( DynamicPublishingPackage.DitaMap m: user.getDynPack().ditaMaps) {
					if (m.file.equals(value)) {
						user.getUserPack().ditaMaps[0].file = m.file;
						user.getUserPack().ditaMaps[0].title = m.title;
						user.getUserPack().ditaMaps[0].task = m.task;
						break;
					}
				}
				break;
			case userEmail:
				user.setUserEmail(value);
				break;
			case previous:
				//took care before
				break;
			case page:
				if(value.startsWith("RelatedDocs"))
					goRelDocs = true; 
			case prod:
			case outFormat:
				session.setAttribute(param.name(), value);
				break;
			case state:
				if ("cancel".equals(value)) {
					cleanUp(session);
					response.sendRedirect("index.jsp");
					return;
				}
				else if ("uploadComplete".equals(value))
					session.setAttribute("state",value);	
				break;
			default:
			}
		}

		// take care of pubLegend 

		if (user.getUserPack().ditaMaps[0].file != null ) {
			if (user.getUserPack().ditaMaps[0].task != null) {
				if (user.getPubLegend().get("title") == null) 
					user.getPubLegend().put("title", user.getUserPack().ditaMaps[0].task);
			}
			else if (user.getUserPack().ditaMaps[0].title != null) {
				if( user.getPubLegend().get("title") == null)
					user.getPubLegend().put("title", user.getUserPack().ditaMaps[0].title);
			}
		}
		
		if (doProfiles) {
			for (int i=0; i<user.getDynPack().profiles.length; i++) {
				user.getUserPack().profiles[i].id = user.getDynPack().profiles[i].id;
				user.getUserPack().profiles[i].name = user.getDynPack().profiles[i].name;
				user.getUserPack().profiles[i].status = user.getDynPack().profiles[i].status;
				if (DynamicPublishingPackage.PROFILE_STATUS.hidden.equals(user.getDynPack().profiles[i].status))
						ToolKit.setUserPackValues(user.getUserPack(), i, user.getDynPack().profiles[i].values, null);
				else {
					ToolKit.setUserPackValues(user.getUserPack(), i, user.getDynPack().profiles[i].values, user.getParameterValues(user.getDynPack().profiles[i].id));
					String dt = user.getProfileAliases().containsKey(user.getUserPack().profiles[i].id)?
		    					user.getProfileAliases().get(user.getUserPack().profiles[i].id):
		    					user.getUserPack().profiles[i].id; 
		    		user.getPubLegend().put("profile_" + user.getUserPack().profiles[i].quesNum, dt);
		    		StringBuilder  pubLegendStr = new StringBuilder();
		    		for (int j=0; j<user.getUserPack().profiles[i].values.length; j++) {
		    			String dd = user.getProfileAliases().containsKey(user.getUserPack().profiles[i].values[j].id)?
		    					user.getProfileAliases().get(user.getUserPack().profiles[i].values[j].id):
		    					user.getUserPack().profiles[i].values[j].id; 
		    			pubLegendStr.append(dd + "; ");
		    		}
		    		user.getPubLegend().put("value_" + user.getUserPack().profiles[i].quesNum, pubLegendStr.substring(0, pubLegendStr.lastIndexOf(";")));
				}
			}
		}

		String productGroupEnv = (String) session.getAttribute("prod");
		if (productGroupEnv == null) {
			session.invalidate();
			response.sendRedirect("index.jsp");
			return;
		}
		
		assert(productGroupEnv != null);


		try {
			prodEnv = new ProdEnvBean(productGroupEnv, bl);
			//session.setAttribute("prodEnv", new ProdEnvBean(productGroupEnv, bl));
			session.setAttribute("prodEnv", prodEnv);
		} catch (DynException e) {
			throw new ServletException("DynException: " + e.getMessage());
		}

		String state = (String) session.getAttribute("state");
		if(!"uploadComplete".equals(state) && !"complete".equals(feedback)) {
			state = getNextState(flow, state, goBack);
			session.setAttribute("state", state);
		}
		
		if(goBack && 
				("displayBooks".equals(state) && flow.equals("reverse")) ||
				("displayQuestions".equals(state) && flow.equals("normal")))
			session.removeAttribute("currentId");
		if(goBack && "displayDelivery".equals(state)) {
			session.setAttribute("state", getNextState(flow, "displayDelivery", goBack));
			session.removeAttribute("currentId");
			try {
				user.pop();
			} catch (DynException e) {
				new ServletException("DynException: " + e.getMessage());
			}
		}
		
		if("displayDelivery".equals(state)) {
			Long resId = -1L;
			// We do not have user choice for output format yet. So, for now we generate pdfs only
			user.getUserPack().outputType = DynamicPublishingPackage.OUTPUT_TYPE.valueOf("pdf2");
			System.out.println("UserPack:\n" + ToolKit.printRequest(user.getUserPack()));			
			try {
				PackageData.validateUserPack(user.getUserPack());
				// Trying to retrieve result ID from the database (using pub request MD5)
				resId = DbWebHelper.getResultId(ToolKit.getMD5(user.getUserPack()));
			} catch (DynException e) {
				throw new ServletException("DynException:" + e.getMessage());
			}
			if (resId > 0)
				session.setAttribute("currentId", Long.toString(resId));
		}
		
		System.out.format("stackSize=%d user.getParameter(state)=%s session.getAttribute(state)=%s%n", user.getStackSize(), user.getParameter("state"), session.getAttribute("state"));
		if(goRelDocs) 
			response.sendRedirect("relatedDocs.jsp");
		else 
			response.sendRedirect("requestMyDoc.jsp");
	}
	
	private static String getNextState(String flow, String currentState, boolean goBack) {
		String state = null;
		
		if(goBack) {
			for(Entry<String, String> entry: flowMap.get(flow).entrySet())
				if(entry.getValue().equals(currentState)) {
					state = entry.getKey();
					break;
				}
		}
		else 
			state = flowMap.get(flow).get(currentState);	
		//To get feedback thank you note
		
		System.out.format("flow=<%s> currenState=<%s> nextState=<%s> goBack=<%s>%n", flow, currentState, state, goBack);
		return state;
	}
		
	private static void cleanUp(HttpSession session) {
		if (session == null)
			return;
		synchronized (session) {
			WebMementoUserBean user = (WebMementoUserBean) session.getAttribute("user");
			if (user != null)
				user.clean();
			ProdEnvBean prodEnv = (ProdEnvBean) session.getAttribute("prodEnv");
			if (prodEnv != null)
				prodEnv.clean();
			session.removeAttribute("pageTitle");
			session.removeAttribute("visibleProfiles");
			session.removeAttribute("profileValueName");
			session.removeAttribute("autoSelect");
			session.removeAttribute("static");
			session.removeAttribute("currentId");
			session.removeAttribute("pdfFileName");
			session.removeAttribute("pdfFileSize");
			session.removeAttribute("flow");
			session.removeAttribute("state");
			session.removeAttribute("feedback");
		}
	}
}
