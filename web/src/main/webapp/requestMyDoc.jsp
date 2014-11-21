		
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.concurrent.Future" %>
<%@ page import="java.util.Arrays, java.util.TreeSet, java.util.HashSet,
java.util.List, java.util.ResourceBundle, java.util.Set, java.util.Vector,
java.util.Date, java.util.Enumeration,
java.util.Locale, java.io.File,	java.util.Map,java.util.HashMap" %> 
<%@ page import="org.apache.commons.lang3.LocaleUtils" %>
<%@ page import="trisoftdp.core.*, trisoftdp.web.core.*" %>
<%@ page import="trisoftdp.web.processing.DynPubJob, trisoftdp.web.processing.DynPubThreadPoolExecutor" %>
<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ page errorPage="dynPubError.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="user" scope="session" class="trisoftdp.web.core.WebMementoUserBean" />
<jsp:useBean id="prodEnv" scope="session" class="trisoftdp.core.ProdEnvBean" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="includes/headHtml.jspf" %>

<body>
<% ///////////////////////////  DYN PUB HEADER ///////////////////////////////////////////// 
String bl = (String) session.getAttribute("lang");
if (request.getCookies() != null) {
	for (Cookie c: request.getCookies())
		if ("appLang".equals(c.getName()))
			bl=c.getValue();
}
if (bl == null) bl="en_US";


String prodPage = (String) session.getAttribute("page");
String prodGroup = (String) session.getAttribute("prod");

%>
<c:set var="lang" value="<%=bl %>" scope="page"/>
<fmt:setLocale value="<%=bl%>" scope="session" /> 
<%
Map<String,String> appStringsMap = new HashMap<String,String>();
Locale l = (Locale) response.getLocale();
CoreConstants.populateMap(appStringsMap, ResourceBundle.getBundle("appStr", l)); %>
<!-- Header Start -->
<div class="parentheader"> 
	<div id="header" >
		<h1><a href="#home" title="EMC2">EMC2</a></h1>
        	<div id="right-toolbar"></div>
        	<div id="navigation"></div>
            <div id="top-toolbar">
<% 
            if ("yes".equals(prodEnv.getProdL10NSupport())) { 
%>
            	<ul id="jsddm">
                	<li><a href="mailto:<%=prodEnv.getProdSupportEmail()%>" class="liNormal"><fmt:message key="header.feedback" /></a></li>                
                    <li><a href="#">
						<fmt:message key="header.lang.${lang}"/>&nbsp;&nbsp;
						<img src="images/arrowDown.gif" />
						</a>
						
                    	<ul>
                        	<li><a href="#">
								<fmt:message key="header.lang.${lang}"/>
								<img src="images/arrowUp.gif" class="langArrow" />
								</a></li>
<%  
							ResourceBundle currentBundle = ResourceBundle.getBundle("appStr", LocaleUtils.toLocale(bl));
							String message = currentBundle.getString("header.lang.change1") + "\\n" + currentBundle.getString("header.lang.change2");
							String[] langSupported = ToolKit.getDisplayLanguages();
							for  ( int i=0; i<langSupported.length; i++) {
								String otherLangCode = langSupported[i].split(":")[0];
								String otherLangName = langSupported[i].split(":")[1].split("\\s+")[0];
								session.setAttribute("otherLangCode", otherLangCode);
								if (!bl.equals(otherLangCode)) {
									Locale loc = new Locale(otherLangCode);
									ResourceBundle bundle = ResourceBundle.getBundle("appStr", loc);
									otherLangName = bundle.getString("header.lang." + otherLangCode);
									if ((String) session.getAttribute("state")== null || "start".equals((String) session.getAttribute("state"))) {
%>
			        					<li><a href="DynDispatcher?lang=<%=otherLangCode%>&state=start">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
<%
									} else {				
%>
			        					<li><a href="DynDispatcher?lang=<%=otherLangCode%>&state=start" onClick = "javascript:return confirm('<%= message%>');">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
<%         							}
        						}
							} 
%>                     
                        </ul>
					</li>
					
                </ul>
<%                 }
            		else {
%>
                <ul id="jsddmNoLoc">
                	<li><a href="mailto:<%= prodEnv.getProdSupportEmail() %>" class="liNormal"><fmt:message key="header.contact" /></a></li>

                </ul>
                
<%                } 
%>
                <div class="clear"> </div>
                
            </div>
    </div>
</div>

<!-- Header End -->

<% ////////////////////////////// END OF DYN PUB HEADER /////////////////////////////////////////

//Check, if the product Environment is set up
//If it is, get the configGroup file and the dir where the dp packages are stored
//If it is not send back to index.jsp

if (prodEnv.getProductDir() == null || prodPage == null) 
	response.sendRedirect("index.jsp");
File dpPackDir = new File(prodEnv.getProdDpPackDir());
File groupsXml = new File(prodEnv.getProdConfigDir() + prodPage + ".xml");
DynPage dynPage = PackageData.getDynamicPage(groupsXml);

String state = (String) session.getAttribute("state"); 
String flow =(String) session.getAttribute("flow"); 
	
if (state == null || "start".equals(state))	{
	session.setAttribute("state","start");

System.out.println("bl = " + bl);
System.out.println(prodEnv.getProdDpPackDir());
////////////// COVER PAGE ///////////////////
%>

<div class="content">
  <div id="ngoe-template">
    <div id="shell">
      <div id="main-title">
      <%if ("yes".equals(CoreConstants.appPropsMap.get("IS_STATIC_MAPPING_SITE"))) { %>
      	<h1>Static Mapping - connected to the <%=WebConstants.webPropsMap.get("APP_ENV") %> database </h1>
      <%} else { %>
        <h1><span id="main_title_span"><%=dynPage.pageTitle %></span></h1>     
      <%} %>
      </div>
      <div class="ngoe-row-top"></div>
      <div class="ngoe-row-content">

<!-- This is a Form part -->
		<div id="splashpage" class="primary"> 
			<div id="ConAndBut">
				<div id="ESAEndUserContent">

<% 	if (!"".equals(dynPage.pageGroupsTitle)) {
%>
					<h2><%=dynPage.pageGroupsTitle %></h2>
					<div id='configList'>
<%	} 
	else {
%>	
					
					<div id='configListNoTitle'>
<%
   }
%>
						<p><%=dynPage.pageIntro %></p>	    
<% 	
// Find all configGroups file names in the product /config folder
// We have to leave in the "Packages without group assignments" only those configs that are not listed
// in all config groups files.

String[] cgFN = new File(prodEnv.getProdConfigDir()).list();
TreeSet<String> confGroupsFileNames = new TreeSet<String>();
for (String cName: cgFN)
	if(cName.toLowerCase().startsWith("configgroups") && cName.toLowerCase().endsWith(".xml"))
		confGroupsFileNames.add(cName);

// Find all dynpacks xml files that located at dpPackDir.

String[] cFileNames = dpPackDir.list();
TreeSet<String> extraFileNames = new TreeSet<String>();
	for(String name: cFileNames)
		if(name.toLowerCase().endsWith(".xml"))
			extraFileNames.add(name.substring(0, name.length()-4));

// Display all the dynpacks for the current dyn page. Eliminate them from the extraFileNames set.

	DynPage.DynPackageGroup[] groups = dynPage.packGroups;
	if (groups != null) {
		System.out.println("Parsing config files in " + dpPackDir);
		for (int i=0; i<groups.length; i++) {
			%>
			<h6><%=groups[i].groupName %></h6>
			<div id="pMargin">
			<%
			for (int j=0; j<groups[i].packs.length; j++) {
				File packConfig = null;
				if(extraFileNames.contains(groups[i].packs[j].packFileName))
					extraFileNames.remove(groups[i].packs[j].packFileName);
				File configXmlFile = new File(dpPackDir + File.separator + groups[i].packs[j].packFileName + ".xml");
				packConfig = configXmlFile;
				if (packConfig != null) {
					DynPackageDescriptor packInfo = PackageData.getPackageDescriptor(packConfig);
					String packName = packInfo.packageName; 
					if ("yes".equals(prodEnv.getProdShowRelease()))
						packName = packName + " " + packInfo.productRelease;
					if (packInfo.link == null) {						
%>
						<h2><a href="DynDispatcher?configId=<%=groups[i].packs[j].packFileName %>"><%= packName  %></a></h2>
						<p class="note"><%=packInfo.comments %></p>
<% 
					} else { 
						String url;
						if (packInfo.relDocXml == null)
							url = packInfo.link;
						else 
							url = packInfo.link + "?rd=" + packInfo.relDocXml;
%>
						<h2><a href="<%=url %>"><%= packName %></a></h2>
						<p class="note"><%=packInfo.comments %></p>
<% 					}	// end of if (packInfo.link == null)
				} 		// end of if (packConfig != null) 
			} 			// end of for (int j=0.....) %>
			</div>
<%		} 				// end of for (int i=0....)
 	}					// end of if (groups != null)

	// Eliminate from extraFileNames set all the dynpacks that present in other ConfigGroups files.

	for (String cgName: confGroupsFileNames) {
		if (!groupsXml.equals(cgName)) {
			DynPage otherPage = PackageData.getDynamicPage(new File(prodEnv.getProdConfigDir() + File.separator + cgName));
			DynPage.DynPackageGroup[] pageGroups = otherPage.packGroups;
			if (pageGroups != null)
				for (int i=0; i<pageGroups.length; i++) 
					for (int j=0; j<pageGroups[i].packs.length; j++) 
						if(extraFileNames.contains(pageGroups[i].packs[j].packFileName))
							extraFileNames.remove(pageGroups[i].packs[j].packFileName);
		}
	}
 	
	// Put all the remaining packages in the red "Packages without group assignment" group
	
	if(extraFileNames.size() > 0) {
%>
			<h6 style="color: red;">The packages without group assignments</h6>
			<div id="pMargin">
<%
		for(String name: extraFileNames) {
			File packConfig = new File(dpPackDir + File.separator + name + ".xml");
			if (packConfig != null) {
				DynPackageDescriptor packInfo = PackageData.getPackageDescriptor(packConfig);
				String packName = packInfo.packageName + " " + packInfo.productRelease;
				if (packInfo.link == null) {						
%>
					<h2><a href="DynDispatcher?configId=<%=name %>"><%= packName  %></a></h2>
					<p class="note"><%=packInfo.comments %></p>
<% 
				} else { 
					String url;
					if (packInfo.relDocXml == null)
						url = packInfo.link;
					else 
						url = packInfo.link + "?rd=" + packInfo.relDocXml;
%>
					<h2><a href="<%=url %>"><%= packName %></a></h2>
					<p class="note"><%=packInfo.comments %></p>
<% 
				}
			} 	
		}
%> 
			</div>
<% 
	}
%>					</div><!-- end of "configList" --> 
			</div><!-- end of "ESAEndUserContent" -->
	
		</div><!-- end of "ConAndBut" -->
		<div id="ESAButPanel">
			<a href="index.jsp" class="ESAbutActive" ><span><fmt:message key="buttons.home" /></span></a>
		</div>
		</div> <!-- end of "splashpage" -->
<!-- End of Form part -->
      </div> <!-- end of "ngoe-content" -->
      <div class="ngoe-row-bottom"></div>
    </div> <!-- end of "shell" -->
  </div> <!-- end of "ngoe-template" -->
</div> <!-- end of "content" -->
</div><!-- Body/Content End -->
	
<%
//////////////// END OF COVER PAGE /////////////////////////////	

		} 
		else {
			
////////////////   WIZARDS     /////////////////////////////////			
%>

<div class="content">
  <div class="ngoe-2col-b" id="ngoe-template">
    <div id="shell">
      <div id="main-title">
        <h1><span id="main_title_span"><%=dynPage.pageTitle %> <fmt:message key="main.dash" /> <%=user.getDynPack().packageName%></span></h1> 
      </div>
      <div class="ngoe-container-top"></div>
        <div class="ngoe-content">
      
<!-- ///////////////////// LEFT PANEL WITH PUB REQUEST /////////////////////////////////////  -->

          <div class="secondary">
            <div class="ngoe-rail-panel first">
              <div class="x-panel-tl">
                <div class="x-panel-tr">
                  <div class="x-panel-tc">
                    <div class="x-panel-header"> 
                  	  <span class="x-panel-header-text"><fmt:message key="request.title" /></span> 
                    </div>
                  </div>
                </div>
              </div>
<%
Date date = new java.util.Date();
pageContext.setAttribute("date", date);
%>
<c:set var="date" value="${date}" />

              <div id="Legend" class="x-panel-body"> 
	            <dl>
	   	          <dt><fmt:message key="request.date" /></dt>
    		      <dd><fmt:formatDate type="date" dateStyle="full" value="${date}"/></dd>
<%
    	if (user.getUserPack() == null) 
    		throw new ServletException("No user pack"); //TODO redirect instead of Exception
%>
    	          <dt><fmt:message key="request.packTitle" /></dt>
    		      <dd><%=user.getUserPack().packageName %></dd>

<%
    	if (user.getUserPack().ditaMaps[0].file != null) {%>
	    	    <dt><fmt:message key="request.docTitle" /></dt>
	        	<dd><%=user.getUserPack().ditaMaps[0].task %></dd>
	        	
	            <dt><fmt:message key="request.langTitle" /></dt>
				<dd><%=user.getUserPack().languages[0] %></dd>
<% 
    	} 
		if(user.getProfIsSet()) {
			String key;
    		for (int i=0; i<user.getUserPack().profiles.length; i++) {
	    		if (user.getUserPack().profiles[i].status != DynamicPublishingPackage.PROFILE_STATUS.hidden  &&
	    			(key = user.getPubLegend().get("profile_" + user.getUserPack().profiles[i].quesNum)) != null) {
%>		    			
		    	  <dt><%= key %></dt>
<% 
		    	  	for (int j=0; j<user.getUserPack().profiles[i].values.length; j++) {
%>
		    	 	<dd><%= user.getProfileAliases().get(user.getUserPack().profiles[i].values[j].id)%></dd>
<%					}
	    		}
    		}    		
    	}
%>
    			</dl>
			</div> 		<!-- End of id="Legend" -->
          </div>		<!-- End of "ngoe-rail-panel first" -->
        </div>			<!-- End of "secondary" -->
        
<!-- /////////////////////// END OF PUB REQUEST /////////////////////////////// -->

<!-- This is a Form part -->
		<div id="splashpage" class="primary"> 
<%
//////////////////////// DISPLAY TASKS (BOOKS) ////////////////////////////////

if ("displayBooks".equals(state)) {
	DynamicPublishingPackage.DitaMap[] availableMaps;
	if(user.getProfIsSet())
		availableMaps = PackageData.getAvailableMaps(user.getDynPack(),user.getUserPack());
	else 
		availableMaps = user.getDynPack().ditaMaps;
	if (user.getDynPack()!= null) {
%>
			<div id="ConAndBut">
				<div id="ESAEndUserContent">
					<form name="mapsAndLangs" id="mapsAndLangs" method="post" action="DynDispatcher">
						<h2><fmt:message key="main.books.title" /></h2>
						<div id='configList'>
							<h6><fmt:message key="main.books.selectDoc" />:</h6>
							<select  onclick="activateBut('butNext','mapsAndLangs');" id="selectMap" name="file" size="<%=availableMaps.length + 1 %>">
<% 
		for (DynamicPublishingPackage.DitaMap m: availableMaps) { 
%>
								<option value="<%=m.file %>"><%=(m.task != null)? m.task : m.title %></option>
<%
		}
%>
							</select>
						</div>
					</form>
					<form name="cancelAll" id="cancelAll" method="post" action="DynDispatcher">
						<input type="hidden" name="state" value="cancel"/>
					</form>
					
					<form name="goPrevious" id="goPrevious" method="post" action="DynDispatcher">
						<input type="hidden" name="previous" value="yes"/>
					</form>
				</div>
			</div>
			<div id="ESAButPanel">
			<c:choose>
				<c:when test="${user.isStackEmpty}">
					<a href="javascript: submitform('goPrevious')" class="ESAbutInactive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
				</c:when>
				<c:otherwise>
					<a href="javascript: submitform('goPrevious')" class="ESAbutActive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
				</c:otherwise>
			</c:choose>
				<a href="#" class="ESAbutInactive" id="butNext"><span><fmt:message key="buttons.next" /></span></a>
				<a href="javascript: submitform('cancelAll')" class="ESAbutActive"><span><fmt:message key="buttons.cancel" /></span></a>
			</div>
<% 
	} else {
%>
	<p>user.getDynPack() is equal null</p>
<%
	}
//////////////////////// DISPLAY QUESTIONS (PROFILES) ////////////////////////////////

	} else if ("displayQuestions".equals(state)) {
// Getting all the profiles that are not "hidden"
// We do not use user bean here, because in the user bean all profiles are stored, even "hidden"

DynamicPublishingPackage.Profile[] visProfiles = (DynamicPublishingPackage.Profile[]) session.getAttribute("visibleProfiles");
String[][] profileVName = (String[][]) session.getAttribute("profileValueName");
if ((user.getDynPack() != null) && (session.getAttribute("visibleProfiles") != null)) {

// Passing to JS all the dependencies 
%>
<script type="text/javascript">
<!--
<%	String jsScript = PackageData.getDepend(user.getDynPack());
	out.println(jsScript); %>
//-->
</script>
<% 
	} 
%>
<!-- This is a Profiles form page -->

			<div id="ConAndBut">
			  <div id="ESAEndUserContent">
<%
	if ((visProfiles != null) && (visProfiles.length > 0) && (profileVName != null)) { 
%>	
			    <form name="profilingAttr" id="profilingAttr" method="post" action="DynDispatcher">
    			  <h2><fmt:message key="main.profiles.title" /></h2>
				    <div id='configList'>
				    <ol>
<% 		for ( int i=0; i< visProfiles.length; i++) {
			String multi;
			String target;
%>
	                  <li><h6><%=visProfiles[i].quesString %></h6>
<% 			if (visProfiles[i].quesInfo != null) { %>
	                    <p class="note"><b><fmt:message key="main.note" />: </b><%=visProfiles[i].quesInfo %></p><% } %>
<% 			for (int j=0; j<visProfiles[i].values.length; j++) {
				if(i == visProfiles.length -1)	{
					target = "null";
					multi = "null";
				}
				else {
					multi = "" + visProfiles[i].values[j].multiselect;
					target = visProfiles[i+1].id;
				}
%>
		                <input type="checkbox"  <%=((i==0)? "":"disabled='disabled'")%> 
		                	id="<%=multi + ":" + target%>"
			                name="<%=visProfiles[i].id%>"
			                value="<%=visProfiles[i].values[j].id %>"
			                onclick="onBoxSelected(this);"/>
		                <%=profileVName[i][j] %><br/>
<%
			}
%>
	</li>
<%
		}
%>	    
	</ol>					
                  </div> <!-- End of 'configList' -->
                  <input type="hidden" name="doProfiles" value="yes"/>
                </form>
<%
	} // End of if ((visProfiles != null) && (profileVName != null))
// Form for the button "Cancel" 
%>
                <form name="cancelAll" id="cancelAll" method="post" action="DynDispatcher">
                  <input type="hidden" name="state" value="cancel"/>
                </form>
<%-- Form for the button "Previous" --%>
                <form name="goPrevious" id="goPrevious" method="post" action="DynDispatcher">
                  <input type="hidden" name="previous" value="yes"/>
                </form>
              </div>		<!-- End of "ESAEndUserContent" -->
            </div> 			<!-- End of "ConAndBut" -->
            <div id="ESAButPanel">
              <c:choose>
	            <c:when test="${user.isStackEmpty}">
		          <a href="javascript: submitform('goPrevious')" class="ESAbutInactive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	            </c:when>	
	            <c:otherwise>
		          <a href="javascript: submitform('goPrevious')" class="ESAbutActive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	            </c:otherwise>
              </c:choose>
	          <a href="#" class="ESAbutInactive" id="butNext"><span><fmt:message key="buttons.next" /></span></a>
	          <a href="javascript: submitform('cancelAll')" class="ESAbutActive"><span><fmt:message key="buttons.cancel" /></span></a>
            </div>
<!-- End of a Profiles form page -->

<% /////////////////////////////////// DELIVERY PAGE ///////////////////////////////////////////
	} else if ("displayDelivery".equals(state)) {
		String formSubmit;
		String curId = (String) session.getAttribute("currentId");
		if (curId != null)
			response.sendRedirect("DynDispatcher");
		else  {
			/* if (!("yes".equals((String)session.getAttribute("static"))))
				session.setAttribute("currentId", "none");
			//curId = (String) session.getAttribute("currentId"); 
			String formSubmit = "deliveryInfo"; */ %>
            <div id="ConAndBut">
              <div id="ESAEndUserContent">
<%  // Static mapping 
			if ("yes".equals(session.getAttribute("static"))) { 
				
	// We will not allow the user to see the form for static mapping
				if (!"yes".equals(CoreConstants.appPropsMap.get("IS_STATIC_MAPPING_SITE"))) {
					formSubmit = "missingStaticContent"; %>
					<h2><fmt:message key="main.delivery.title" /></h2>				
						<form id="missingStaticContent" name="missingStaticContent" action="DynPubMissingStaticContent" method="post" onsubmit="javascript:return validate('missingStaticContent','userEmail');">					
							<div id='configList'>
								<p style="color:red;"><fmt:message key="static.missing.red"/></p>
								<p><fmt:message key="static.missing.statement"/></p>
								<p><fmt:message key="main.delivery.email" />&nbsp;<fmt:message key="main.delivery.email.optional"/></p>
								<p><input type="text" size="100" name="userEmail" id="userEmail" value="" /></p>									
							</div>
						</form>
					
	<%			} else {
				Date d = new Date();
				long id = d.getTime();
				curId = Long.toString(id);
				session.setAttribute("currentId", curId);
	   			formSubmit = "uploadPDF"; %>
	   			
	          <form id="uploadPDF" name="uploadPDF" action="DynPubServletFileUpload" enctype="multipart/form-data" method="POST">
		        <h2><fmt:message key="main.delivery.title" /></h2>
	            <div id='configList'>
			    	<p>Our records indicate that this publishing request requires <b>static PDF document</b> to be uploaded in the database. </p>
			    	<p>Please choose the file that would associate with this request:</p>
					<input type="file" name="pdfStatic" size="60"/> 
			   		<input type="hidden" name="outFormat" value="pdf2" />
			   		<input type="hidden" name="docId" value="<%=curId %>"/>
				</div>
			  </form>
			  <div id="progress">
	            <img alt="progress" src="images/animated_progress.gif" />
	            <p>Wait while the file is uploaded...</p>
	          </div>
<%				}
			} // If not static mapping
			else {
				session.setAttribute("currentId", "none");
				formSubmit = "deliveryInfo"; %>
	          <form name="deliveryInfo" id="deliveryInfo" method="post" action="DynDispatcher"
			        onsubmit="javascript:return validate('deliveryInfo','userEmail');">
	            <h2><fmt:message key="main.delivery.title" /></h2>
	            <div id='configList'>
	   	          <p><fmt:message key="main.delivery.format" /></p>
<%-- Commented out is the choice of the format, no choice now, only pdf2 --%>	   	          
	   			  <p><fmt:message key="main.delivery.email" />:</p>
	   	          <input type="text" size="100" name="userEmail" id="userEmail" value="" /> 
	   	          <input type="hidden" name="outFormat" value="pdf2" />
	   	        </div>
	          </form>
<%			}
%>  
	          <form name="cancelAll" id="cancelAll" method="post" action="DynDispatcher">
		        <input type="hidden" name="state" value="cancel" />
	          </form>
<%-- Form for the button "Previous" --%>
              <form name="goPrevious" id="goPrevious" method="post" action="DynDispatcher">
                <input type="hidden" name="previous" value="yes"/>
              </form>	     
            </div> <!-- End of "ESAEndUserContent" -->
          </div> <!-- End of "ConAndBut" -->
          <div id="ESAButPanel">
           <c:choose>
	         <c:when test="${user.isStackEmpty}">
		       <a href="javascript: submitform('goPrevious')" class="ESAbutInactive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	         </c:when>	
	         <c:otherwise>
		       <a href="javascript: submitform('goPrevious')" class="ESAbutActive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	          </c:otherwise>
            </c:choose>
	        <a href="javascript: submitform('<%=formSubmit %>')" class="ESAbutActive" id="butNext"><span><fmt:message key="buttons.finish" /></span></a>
	        <a href="javascript: submitform('cancelAll')" class="ESAbutActive" id="butCancel"><span><fmt:message key="buttons.cancel" /></span></a>
          </div>
<%
		// If we know the doc ID from the DB, go to the Displaying the result page
		} 		
//////////////////////////// END OF DELIVERY PAGE //////////////////////////

//////////////////////////// DISPLAYING THE RESULT PAGE ////////////////////
	} 
	else if ("finish".equals(state)) {
		String outFormat = (String) session.getAttribute("outFormat"); 
		String feedback = (String) session.getAttribute("feedback");
%>
	<div id="ConAndBut">
	  <div id="ESAEndUserContent">
	    <h2><fmt:message key="main.delivery.title" /></h2>
	    <%
// If it is a Thank you note for the feedback
		if ("complete".equals(feedback)) {
		session.removeAttribute("feedback");   %>
		<div id='configList'>
			<p><fmt:message key="feedback.thankyou"/></p>
		</div>
		<% } else {%>
	    <div id='configList'>

<%		String docId = (String) session.getAttribute("currentId");
		
// If the new pdf should be generated, 
// the thread starts, and
// the user is getting the Thank you for submitting message	
		if("none".equals(docId)) {
			if(user.getConfigId() == null)
				throw new ServletException("user.getConfigId() == null");
			if(user.getUserPack() == null)
				throw new ServletException("user.getUserPack() == null");
			if(user.getUserEmail() == null)
				throw new ServletException("user.getUserEmail() == null");
			if(user.getPubLegend() == null)
				throw new ServletException("user.getPubLegend() == null");
			log("thread created and about to start");
// 			ProcessThread thread = new ProcessThread(user, prodEnv, appStringsMap, (String) session.getAttribute("lang"));		
// 			thread.start();

//TODO expect  CloneNotSupportedException
			DynPubJob job = new DynPubJob(user.extractUserBean(), prodEnv, appStringsMap, (String) session.getAttribute("lang"));			
			DynPubThreadPoolExecutor.getExecutor().submit(job);		
%>
          <p><fmt:message key="main.delivery.thanks1" /></p>
	      <p><fmt:message key="main.delivery.thanks2" /></p>
<%
	 		String[] emails = user.getUserEmail().split(";");
	 		if (emails.length>1) {
%>
	   	  <p><fmt:message key="main.delivery.theEmails" />:</p>
	   	  <div id="pMargin">
<% 				for (String address : emails) { 
%> 
	   		<p class="pi"> <i><%=address %></i></p> 
<% 				} 
%>
	   	  </div>
<% 			} else { 
%>
	   	  <div id="pMargin"><p><b><fmt:message key="main.delivery.theEmail" /> </b><i><%=emails[0]%></i></p></div>
<% 			}		 
		} else {
// If the pdf exists, the user is getting the link to it	
%>
	&nbsp;
	<%=DynPubNotifications.getCompleteMessage(Long.parseLong(docId),user.getUserPack(), appStringsMap, prodEnv)%>
<% 	    }
%>

		</div>
<%-- /// FEEDBACK FORM /// --%>
<%
ResourceBundle bundle = ResourceBundle.getBundle("appStr", LocaleUtils.toLocale((String) session.getAttribute("lang")));
String emailDefaultText = "E-mail:";
String textAreaDefaultText = "Feedback:"; 
String yes = bundle.getString("feedback.yes");
String no = bundle.getString("feedback.no");
String browsing = bundle.getString("feedback.browsing");
%>
<!-- 
<p><fmt:message key="main.delivery.email" />&nbsp;<fmt:message key="main.delivery.email.optional"/></p>
<p><input type="text" size="100" name="userEmail" id="userEmail" value="" /></p>
 -->
<form name="feedback" id="feedback" method="post" action="sendFeedback">
<br><br>
<hr width="80%" align="left" noshade="noshade" size="1"/>  
<div id="ConAndBut">	
<br><br>
	<p><h4><b><fmt:message key="feedback.main.text" /></b></h4></p>
	<table>
		<tr>

		<% if (user.getUserEmail() == null) { %>
			<td colspan=2 align="left" style="padding-top: 10px; ">
	   			  <p><%=emailDefaultText %></p>
	   	          <p><input type="text" size="79" name="userEmail" id="userEmail" value="" style="margin:0px;" /> 
			</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		<%} %>
		</tr>	
		<tr>
			<!--  <td><textarea cols="50" rows="5" name=txtFeedback value=""><fmt:message key="feedback.textbox.text" /></textarea></td>-->
			<td>
			<!--<textarea name="txtFeedback" cols="50" rows="5" onblur="if (this.value == '') {this.value = '<%=textAreaDefaultText %>';}"   onfocus="if (this.value == '<%=textAreaDefaultText %>') {this.value = '';}"><%=textAreaDefaultText %></textarea>-->
			<p><%=textAreaDefaultText %></p>
			<textarea name="txtFeedback" cols="50" rows="5" ></textarea>
			</td>
			<td width="50px"></td>
			<td>
				<p align="center"><fmt:message key="feedback.excellent" /></p>
				<input type="radio" name=rating value="++"/><label> + +</label><br>
				<input type="radio" name=rating value="+" /><label> +</label><br>
				<input type="radio" name=rating value="+-"/><label> + -</label><br>
				<input type="radio" name=rating value="-" /><label> -</label><br>
				<input type="radio" name=rating value="--"/><label> - -</label><br>
				<p align="center"><fmt:message key="feedback.poor" /></p>
			</td>
		</tr>
		<tr>
			<td colspan=3>
				<table>
					<tr>
						<td><fmt:message key="feedback.problemsolved"/></td>
						<td><input type="radio" name="problemsolved" value="<%=yes %>"/><label><fmt:message key="feedback.yes"/></label></td>
						<td><input type="radio" name="problemsolved" value="<%=no %>"/><label><fmt:message key="feedback.no"/></label></td>
						<td><input type="radio" name="problemsolved" value="<%=browsing %>"/><label><fmt:message key="feedback.browsing"/></label></td>
					</tr>
				</table>
			</td>
		</tr>		
	</table>	
	</div>	
	<hr width="80%" align="left" noshade="noshade" size="1"/>  	
</form>


<%-- /// END OF FEEDBACK FORM /// --%>		
			
<%} %>		
  	    <form name="goPrevious" id="goPrevious" method="post" action="DynDispatcher">
          <input type="hidden" name="previous" value="yes"/>
        </form>
	    <form name="cancelAll" id="cancellAll" method="post" action="DynDispatcher">
		  <input type="hidden" name="state" value="cancel"/>
	    </form>
	  </div> <!-- End of "ESAEndUserContent" -->
	</div>
	<div id="ESAButPanel">
		<c:choose>
	      <c:when test="${user.isStackEmpty}">
		    <a href="javascript: submitform('goPrevious')" class="ESAbutInactive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	      </c:when>	
	      <c:otherwise>
		    <a href="javascript: submitform('goPrevious')" class="ESAbutActive" id="butPrevious"><span><fmt:message key="buttons.previous" /></span></a>
	      </c:otherwise>
        </c:choose>
		<a href="javascript: submitform('cancelAll')" class="ESAbutActive"><span><fmt:message key="buttons.home" /></span></a>
		<!-- Below button added by Chaya Somanchi for TDP 1.1b requirement 5.1.3.Improve existing user feedback visibility -->
		<%if (!"complete".equals(feedback)) { %>
			<a href="javascript: submitform('feedback');" class="ESAbutActive"><span><fmt:message key="buttons.submitFeedback" /></span></a>
		<%} %>
	</div>
<%-- //////////////////////////// END OF DISPLAYING THE RESULT PAGE ////////////////////

//////////////////////////// UPLOAD COMPLETE PAGE (USED FOR STATIC MAPPING) ////////////////////
--%>
<%
	}
	else if ("uploadComplete".equals(state)) {
		String uploadedFile = (String) session.getAttribute("pdfFileName");
		String uploadedFileSize = (String) session.getAttribute("pdfFileSize");
		String dId = (String) session.getAttribute("currentId");
		String dbFileName = user.getUserPack().ditaMaps[0].file;

//Processing code
		if (uploadedFile != null) {%>
    <div id="ConAndBut">	
	  <div id="ESAEndUserContent">
        <h2>Delivery</h2>
	    <div id='configList'>
	      <p>Upload complete.</p>
	   	  <div id="pMargin">
	   		<p><b>File Name:</b> <%=uploadedFile %> </p>
	   		<p><b>File Size:</b> <%=uploadedFileSize %></p> 
	   		<p><b>Document ID in the database:</b> <%=dId %></p>
	   		<p><a href="http://<%=CoreConstants.HOST %>/<%=CoreConstants.appPropsMap.get("APP_DIR") %>/Result?docId=<%=dId %>" rel="<%=prodEnv.getProductDir() %>"><%=dbFileName %>.pdf</a></p>
	   	  </div>
	    </div>	
	    <form name="cancelAll" id="cancellAll" method="post" action="DynDispatcher?prod=<%=prodGroup%>&page=<%=prodPage %>">
		  <input type="hidden" name="state" value="cancel"/>
	    </form>
	  </div>
    </div>
	<div id="ESAButPanel">
		<a href="javascript: submitform('cancelAll')" class="ESAbutActive"><span>HOME</span></a>
	</div>
<%		} //end of if (uploadedFile != null)
	}
%>
</div>
<!-- End of Form part -->
		
        <div class="clrboth"></div>
      </div> <!-- end of "ngoe-content" -->
      <div class="ngoe-container-bottom"></div>
    </div> <!-- end of "shell" -->
  </div> <!-- end of "ngoe-2col-b" -->
</div> <!-- end of "content" -->
<!-- Body/Content End -->
<% } // end of Wizards
%>
<%
String foot = "includes/footer" + WebConstants.webPropsMap.get("APP_ENV") + ".jspf";
%>
<div id="footer">
  <div class="foot_copyright">&copy;&nbsp;<fmt:message key="email.copyright"/></div>
  <div class="fl foot_mar"></div>
</div>
<jsp:include page="<%=foot%>" flush="true"/>

 
</body>
</html>
