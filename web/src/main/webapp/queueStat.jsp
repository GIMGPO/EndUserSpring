<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@ page import="trisoftdp.processing.DynPubThreadPoolExecutor" %> 
<%@ page import="java.util.Date" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Queue Statistics</title>
</head>
<body>
<%
int runningJobsCount = DynPubThreadPoolExecutor.getRunningJobCount();
long allJobsCount =  DynPubThreadPoolExecutor.getAllJobCount();
%>
<p><%=new Date()%></p>
<p>Ever submitted requests <%=allJobsCount%></p>
<p>Running jobs requests <%=runningJobsCount%></p>
</body>
</html>