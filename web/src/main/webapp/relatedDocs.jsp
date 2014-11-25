<%--
	Created by N. Shadrina, 2013
	The related documents page that processes the RelatedDocs_XXX.xml file.
	Called from DynDispatcher, if the user comes from index.jsp
	Or the user comes here directly from a particular dynamic page, 
		the request parameter "rd" should be present and prodEnv should be in session.

--%>
<%@ page import="trisoftdp.core.PackageData" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page errorPage="dynPubError.jsp"%>
<%@ page import="trisoftdp.core.*, trisoftdp.web.core.*, java.util.*,java.io.File, java.util.regex.*, org.apache.commons.lang3.LocaleUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<jsp:useBean id="prodEnv" scope="session" class="trisoftdp.core.ProdEnvBean" />
<%@ include file="./includes/headHtml.jspf" %>
<body>
<% session.setAttribute("state","reldocs");
   String prod = (String) session.getAttribute("prod");
%>
<% ///////////////////////////  DYN PUB HEADER ///////////////////////////////////////////// 
String bl = (String) session.getAttribute("lang");
if (request.getCookies() != null) {
	for (Cookie c: request.getCookies())
		if ("appLang".equals(c.getName()))
			bl=c.getValue();
}
if (bl == null) bl="en_US";
ResourceBundle currentBundle = ResourceBundle.getBundle("appStr", LocaleUtils.toLocale(bl));
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
            <% 
            if ("yes".equals(prodEnv.getProdL10NSupport())) { 
            	if (!"admin".equals((String) session.getAttribute("state"))) {
            	 %>
            	<ul id="jsddm">
                	<li><a href="mailto:<%=prodEnv.getProdSupportEmail() %>" class="liNormal"><span><fmt:message key="header.feedback" /></span></a></li>       
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
			        					<li><a href="index.jsp?lang=<%=otherLangCode%>&state=cancel">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
			        					<%
									} else {				
										%>
			        					<li><a href="index.jsp?lang=<%=otherLangCode%>&state=cancel" onClick = "javascript:return confirm('<%= message%>');">
			        						<fmt:message key="header.lang.${otherLangCode}"/>&nbsp;-&nbsp;<%=otherLangName%>
										</a></li>
        					<%		}
        						}
							} %>                     
                        </ul>
                        <%} %>
					</li>
					
                </ul>
                <%} else {%>
                <ul id="jsddmNoLoc">
                	<%-- <li><a href="mailto:<%= prodEnv.getProdSupportEmail() %>" class="liNormal"><fmt:message key="header.contact" /></a></li>   --%>
                	<!-- Below line added by Chaya Somanchi for the TDP 1.1b requirement 5.1.3.Improve existing user feedback visibility -->
                	<li><a href="mailto:<%= prodEnv.getProdSupportEmail() %>" class="ESAbutActive"><span><fmt:message key="header.contact" /></span></a></li>
                </ul>
                
                <%} %>
                <div class="clear"> </div>
                
            </div>
    </div>
</div>

<!-- Header End -->

<% ////////////////////////////// END OF DYN PUB HEADER /////////////////////////////////////////
   //	String bl = (String) session.getAttribute("lang");

Pattern malicious = Pattern.compile(".*[><'\"\n\r\\(\\)].*");
String rdTitle = "";
String relDocDir = prodEnv.getProdRelDocsDir();
String relDocXml = (request.getParameter("rd") == null)? (String) session.getAttribute("page") :
	request.getParameter("rd");
if (relDocXml != null) {
Matcher mtch = malicious.matcher(relDocXml);
if(mtch.matches())
	throw new DynException("RelDoc file name has malicious characters: " + relDocXml);
}

File relDocFile = new File(prodEnv.getProdConfigDir() + File.separator + relDocXml + ".xml");
if(!relDocFile.exists())
	throw new ServletException("File does not exist " + relDocFile);
DynRelatedDocs drd = PackageData.getRelatedDocs(relDocFile);
// Will need this title in session for the zip "Download All" file name.
session.setAttribute("rdTitle", drd.rdTitle);
if(drd == null)
	throw new ServletException("drd == null for " + relDocFile);
%>
<div class="content">
  <div id="ngoe-template">
    <div id="shell">
      <div id="main-title">
        <h1><span id="main_title_span">
        <%=drd.rdTitle %>
        </span></h1>     
      </div>
      <div class="ngoe-row-top"></div>
      <div class="ngoe-row-content">

<!-- This is a Form part -->
		<div id="splashpage" class="primary"> 
			<div id="ConAndBut">
				<div id="ESAEndUserContent">
					<h2><fmt:message key="reldocs.title" /></h2>
					<div id="configList">
					<%if (!"en_US".equals(bl)) { %>
					<p><fmt:message key="reldocs.transNotAvalable" /></p>	
					<%} %>    

<%
		if (drd != null) { 
    		for (DynRelatedDocs.DocGroup dg: drd.docGroups) {
%>
			<h6><%=dg.groupName%></h6>
			<div id="pMargin">
<%
				for (DynRelatedDocs.RelDoc rd : dg.relDocs) {
					if (DynRelatedDocs.FILE_TYPE.pdf.equals(rd.fileType)) {
%>
			<h4><a href="RelDoc?prod=<%=prod %>&file=<%=rd.docFile %>"><%=rd.docName %></a></h4>
<%					} else { 
%>			<h4><a href="javascript:popupInstructions('zipInstructions.jsp?zipFile=<%=rd.docFile %>&helpFolder=<%=rd.helpFolder %>&helpFile=<%=rd.helpFile %>');"><%=rd.docName %></a></h4>
<%					}
 				}
%> 			</div> <%
		}
%> 					</div><!-- end of "configList" --> <%
	}
%>				</div><!-- end of "ESAEndUserContent" -->

			

		</div><!-- end of "ConAndBut" -->
		<div id="ESAButPanel">
			<a href="index.jsp" class="ESAbutActive"><span><fmt:message key="buttons.home" /></span></a>
			<a href="RelDoc?prod=<%=prod %>&file=all&xmlFile=<%=relDocXml%>" onclick="return confirm('<%=currentBundle.getString("reldocs.downloadAllAlert") %>');" class="ESAbutActive"><span><fmt:message key="buttons.downloadAll" /></span></a>
		</div>
		</div> <!-- end of "splashpage" -->
<!-- End of Form part -->
		<div class="clrboth"></div>
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