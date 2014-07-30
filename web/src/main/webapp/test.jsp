
<%@ page import='java.io.FileReader'%>
<%@ page import="java.io.File" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>


</head>
<body>
Read permission test: <br/>
<pre>

</pre>
Write permission test: <br/>
<%
File newFile =new File("\\\\nashapp204s\\esa\\test\\newFile.txt");
if(!newFile.exists()) {
	newFile.createNewFile();
	out.print("File " + newFile + " created\n");
}
else {
	newFile.delete();
	out.print("File " + newFile + " deleted\n");
}
%>
Reading file test: <br/>

</body>
</html>