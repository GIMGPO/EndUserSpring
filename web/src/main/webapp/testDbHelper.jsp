<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@ page import="trisoftdp.web.core.DbWebHelper" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Test DbHelper</title>
</head>
<body>

<%
long id = DbWebHelper.getResultId("md5");
out.println(String.format("id=%d%n", id));
 %>

</body>
</html>