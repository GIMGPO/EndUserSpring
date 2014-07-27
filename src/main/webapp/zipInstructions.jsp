<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page errorPage="dynPubError.jsp"%>
<%@ page import="trisoftdp.core.*,java.util.Arrays,java.util.HashSet,
java.util.List,java.util.Set,java.util.Vector,java.util.Locale,
org.apache.commons.lang3.LocaleUtils, java.util.ResourceBundle" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>EMC My Documents
<%=(session.getAttribute("pageTitle") != null)?  (":: " + session.getAttribute("pageTitle")):"" %>
</title>

<link rel="stylesheet" type="text/css" href="./css/headerDefault.css" />
<link rel="stylesheet" type="text/css" href="./css/helperClasses.css"  />
<!--[if IE]>
	<link rel="stylesheet" type="text/css" href="./css/dynPubStylesIE6.css" />
<![endif]-->
<!--[if !IE]><!-->
	<link rel="stylesheet" type="text/css" href="./css/dynPubStyles.css"  />	
 <!--<![endif]-->

</head>
<body>

<div class="popUp"> 
	<div id="ConAndBut">
		<div id="ESAEndUserContent">
			<h2><fmt:message key="reldocs.instructons.title" /></h2>
				<div id="configList">	    
<%
	    String lang = (String) session.getAttribute("lang");
	    Locale loc = LocaleUtils.toLocale(lang);
	    String zipFileStr;
	    ResourceBundle bundle = ResourceBundle.getBundle("appStr", loc);
	    	String zipFile = (String) request.getParameter("zipFile");
	    	zipFileStr = "<a href=\"RelDoc?file=" + zipFile + "\">" + zipFile + "</a>";
	    	String step1Str = bundle.getString("reldocs.instructions.step1").replace("***zip_file***", zipFileStr);
	    	String helpFolder = (String) request.getParameter("helpFolder");
	    	String helpFile = (String) request.getParameter("helpFile");
	    	String step4Str = bundle.getString("reldocs.instructions.step4").replace("***folder_name***",helpFolder);
	    	step4Str = step4Str.replace("***Index_name***",helpFile);
	    	if (zipFile != null) {
	    %>
		<ol>
			<li><%=step1Str%></li>
			<li><fmt:message key="reldocs.instructions.step2" /></li>
			<li><fmt:message key="reldocs.instructions.step3.p1" />
				<p class="note"><b><fmt:message key="main.note" />: </b>
				<fmt:message key="reldocs.instructions.step3.p2" /></p></li>
			<li><%=step4Str%></li>
		</ol>
	<%
		}
	%> 				</div><!-- end of "configList" --> 
		</div><!-- end of "ESAEndUserContent" -->
		<div id="ESAButPanel">
			<a href="javascript:self.close();" class="ESAbutActive"><span><%=CoreConstants.appStringsMap.get("buttons.close")%></span></a>
		</div>			
	</div><!-- end of "ConAndBut" -->
</div>
</body></html>