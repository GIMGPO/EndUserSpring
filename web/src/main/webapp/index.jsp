<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page errorPage="dynPubError.jsp"%>
<%@ page import="trisoftdp.core.*, trisoftdp.web.core.*" %>
<%@ page import="java.io.File, java.util.regex.*, org.apache.commons.lang3.LocaleUtils, java.util.ResourceBundle, java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<jsp:useBean id="user" scope="session" class="trisoftdp.web.core.WebMementoUserBean" />
<jsp:useBean id="prodEnv" scope="session" class="trisoftdp.core.ProdEnvBean" />
<%@ include file="./includes/headHtml.jspf" %>

<body>

<% ///////////////////////////  DYN PUB HEADER ///////////////////////////////////////////// 
String bl = (String) session.getAttribute("lang");
if (request.getCookies() != null) {
	for (Cookie c: request.getCookies())
		if ("appLang".equals(c.getName()))
			bl=c.getValue();
}
if (bl == null) bl="en_US";
%>
<c:set var="lang" value="<%=bl %>" scope="page"/>
<fmt:setLocale value="<%=bl%>" scope="session" /> 
<!-- Header Start -->
<div class="parentheader"> 
	<div id="header" >
		<h1><a href="#home" title="EMC2">EMC2</a></h1>
			<!--<div id="downtime">The <i>MyDocuments</i> web site will be down for maintenance <br/>on Saturday, November, 22, 2014 from 09:00 to 11:00 EST </div>-->
        	<div id="right-toolbar"></div>
        	<div id="navigation"></div>
            <div id="top-toolbar">
            	<ul id="jsddm"> 
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
			        					<li><a href="DynDispatcher?lang=<%=otherLangCode%>&state=cancel">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
			        					<%
									} else {				
										%>
			        					<li><a href="DynDispatcher?lang=<%=otherLangCode%>&state=cancel" onClick = "javascript:return confirm('<%= message%>');">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
        					<%		}
        						}
							} %>                     
                        </ul>
					</li>					
                </ul>
                <div class="clear"> </div>
                
            </div>
    </div>
</div>

<!-- Header End -->

<% ////////////////////////////// END OF DYN PUB HEADER /////////////////////////////////////////
//session.invalidate();
File prodGroupsXml = new File(CoreConstants.appPropsMap.get("CONFIG_DIR") + File.separator + CoreConstants.languagesMap.get(bl) + File.separator + "productGroups.xml");
System.out.println("prodGroupsXml = " + prodGroupsXml.getPath());
DynProductGroup[] groups = ProductGroupData.getAllProductGroups(prodGroupsXml);
%>


<div class="content">
  <div class="" id="ngoe-template">
    <div id="shell">
      <div id="main-title">
        <h1><span id="main_title_span"><fmt:message key="cover.welcome"/></span>
        </h1>     
      </div>
      <div class="ngoe-row-top"></div>
      <div class="ngoe-row-content">

<!-- This is a Form part -->
		<div id="splashpage" class="primary"> 
			<div id="ConAndBut">
				<div id="ESAEndUserContent">
<!--  				<div id="ESARelLinksPanel">
						<h6><a href="relatedDocs.jsp"><fmt:message key="welcome.relDocs" /></a></h6>
					</div> 
-->
<div id='configListNoTitle'>    
<% 	
	if (groups != null) {
		for (int i=0; i<groups.length; i++) {
			// for prod groups with multiple configGroups files
			String divHide = "diveHide" + i;
			if (!groups[i].singleGroup) {
				String lToggle = "lToggle" + i;
				String pToggle = "pToggle" + i;	
				String pProdNote = "pProdNote" + i;
				String pMGroupNote = "pMGroupNote" + i;
				String current  = "" + i;
				%>
				<script>
				$(document).ready(function(){
					$(".<%=pToggle%>").hide();  
					$(".<%=pProdNote%>").show();
					$(".<%=pMGroupNote%>").hide();
					$(".<%=lToggle%>").click(function() {
					    $(".<%=pToggle%>").toggle();
					    $(".<%=pProdNote%>").toggle();
					    $(".<%=pMGroupNote%>").toggle();
					    for (var k=0; k < <%=groups.length%>; k++) {
						    	if (k !=<%=i %>) {
					 	   		$('.diveHide' + k).toggle();
					 	   }
					 	}
					  });
				});
				</script>
				<div class="<%=divHide %>">
					<h2><a href="#" class="<%=lToggle%>"><%=groups[i].prodTitle %></a></h2>
					<%if (!"".equals(groups[i].prodNote)) {%>
					<p class="<%=pProdNote%>"><%=groups[i].prodNote %></p>
					<%} %>
					<%if (!"".equals(groups[i].multiGroupNote)) {%>
					<p class="<%=pMGroupNote%>"><%=groups[i].multiGroupNote %></p>
					<%} %>
					<div id="pMargin" class="<%=pToggle%>">
					<%
					for (int j=0; j<groups[i].pages.length; j++) {
						String pageName = groups[i].pages[j].pageName;
						String pageType = ("dynamic".equals(groups[i].pages[j].pageType.toString())) ? "ConfigGroups" : "RelatedDocs";
						String pageConfig = pageType + "_" + groups[i].pages[j].pageKey;
						%>
								<h2><a href="DynDispatcher?prod=<%=groups[i].prodFolder %>&page=<%=pageConfig %>"><%= pageName  %></a></h2>
								<p class="note"><%=groups[i].pages[j].pageDesc %></p>
								<% 
							
						} 
					%> 
					</div> 
				</div>
<% 			} //multiple configGroups finish
			else { 
				String pageType = ("dynamic".equals(groups[i].pages[0].pageType.toString())) ? "ConfigGroups" : "RelatedDocs"; %>
				<div class="<%=divHide %>">
				<h2><a href="DynDispatcher?prod=<%=groups[i].prodFolder %>&page=<%=pageType + "_" + groups[i].pages[0].pageKey %>"><%=groups[i].prodTitle %></a></h2>
<% 				if (!"".equals(groups[i].prodNote)) { %>
					<p><%=groups[i].prodNote %></p> 
<%				} %>
				</div>
<%			}
		}				
	} // end of if(groups != null)
%> 
	</div><!-- end of "configList" --> 
	</div><!-- end of "ESAEndUserContent" -->
	</div><!-- end of "ConAndBut" -->
	<div><p>&nbsp;</p> 
	</div>
	</div> <!-- end of "splashpage" -->
<!-- End of Form part -->
      </div> <!-- end of "ngoe-content" -->
      <div class="ngoe-row-bottom"></div>
    </div> <!-- end of "shell" -->
  </div> <!-- end of "ngoe-template" -->
</div> <!-- end of "content" -->
<!-- Body/Content End -->

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
