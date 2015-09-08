<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.Date, java.util.Locale, java.util.Map, java.util.HashMap, java.util.ResourceBundle" %>
<%@ page import="trisoftdp.web.core.WebConstants, trisoftdp.core.CoreConstants" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page isErrorPage="true" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>EMC My Documents</title>
<link rel="stylesheet" type="text/css" href="./css/footer.css"  />
<link rel="stylesheet" type="text/css" href="./css/headerDefault.css" />
<link rel="stylesheet" type="text/css" href="./css/helperClasses.css"  />
<!--[if IE]>
	<link rel="stylesheet" type="text/css" href="./css/bodyTemplateIE6.css" />
	<link rel="stylesheet" type="text/css" href="./css/railPanelIE6.css" />
	<link rel="stylesheet" type="text/css" href="./css/dynPubStylesIE6.css" />
<![endif]-->
<!--[if !IE]><!-->
	<link rel="stylesheet" type="text/css" href="./css/bodyTemplate.css" />
	<link rel="stylesheet" type="text/css" href="./css/railPanel.css"  />
	<link rel="stylesheet" type="text/css" href="./css/dynPubStyles.css"  />	
 <!--<![endif]-->

<script language="javascript" src="js/ESApubUser.js"></script>
</head>
<body>
<% 
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
CoreConstants.populateMap(appStringsMap, ResourceBundle.getBundle("appStr", l));
%>
<!-- Header Start -->
<div class="parentheader"> 
	<div id="header" >
		<h1><a href="#home" title="EMC2">EMC2</a></h1>
        	<div id="right-toolbar"></div>
        	<div id="navigation"></div>
            <div id="top-toolbar">
            	<div id="user-bar">
            		<a href="mailto:<%=trisoftdp.web.core.WebConstants.webPropsMap.get("DEFAULT_EMAIL")%>" class="liNormal"><span><fmt:message key="header.feedback" /></span></a>  
            	</div>
            </div>
    </div>
</div>
<!-- Header End -->

<div class="content">
  <div class="" id="ngoe-template">
    <div id="shell">
      <div id="main-title">
		<h1><span id="main_title_span"><fmt:message key="error.mainTitle" /></span></h1> 
      </div>
      <div class="ngoe-row-top"></div>
      <div class="ngoe-row-content">

<!-- This is a Form part -->
<div id="splashpage" class="primary"> 
	<div id="ConAndBut">
		<div id="ESAEndUserContent">
			<h2 style="color:red;"><fmt:message key="error.title"/></h2>
			<div id='configList'>
				<p>
				<fmt:message key="error.p1"/> <br/>
				<i style="color:red;"><fmt:message key="error.p1a"/></i> <br/>
				<fmt:message key="error.p1b"/> <br/>
				</p>
				<p>
				<fmt:message key="error.p2"/> <br/>
				<fmt:message key="error.p3"/>&nbsp;
				<a href="mailto:<%=trisoftdp.web.core.WebConstants.webPropsMap.get("DEFAULT_EMAIL")%>">
					<span><fmt:message key="header.contact"/></span></a>
  				</p>
<%
if (!"PRD".equals(trisoftdp.web.core.WebConstants.webPropsMap.get("APP_ENV"))) {
%> 				
  				<br/>
				<p><b>Java class: </b> <%= exception.getClass() %></p>
				<p><b>Message: </b><pre> <%= exception.getMessage() %></pre></p>
<%
	//out.println("<!--");
	
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	exception.printStackTrace(pw);
	out.print("<pre>" + sw + "</pre>");
	sw.close();
	pw.close();
	
	//out.println("-->");

}
%>
			</div>
		</div>
		
	</div> <!-- End of ConAndBut -->
	<div id="ESAButPanel">
			<a href="index.jsp" class="ESAbutActive"><span>START OVER</span></a>
	</div>
	<div><p>&nbsp;</p> </div>
</div>
<!-- End of Form part -->
      </div> <!-- end of "ngoe-content" -->
      <div class="ngoe-row-bottom"></div>
    </div> <!-- end of "shell" -->
  </div> <!-- end of "" -->
</div> <!-- end of "content" -->
<!-- Body/Content End -->

<%
String foot = "includes/footer" + trisoftdp.web.core.WebConstants.webPropsMap.get("APP_ENV") + ".jspf";
%>

<jsp:include page="<%=foot%>" flush="true"/>
<div id="footer">
  <div class="foot_copyright">&copy;&nbsp;<fmt:message key="email.copyright"/></div>
  <div class="fl foot_mar"></div>
</div>
<% if (!session.isNew()) try { session.invalidate(); } catch(Exception e) {}  %>
</body>
</html>