<%--
	Created by N. Shadrina, Feb 2014
	This page is created for the processing of url when the user comes 
	from outside the application and wants to see particular dynpack.
	The url has to have 3 parameters:
		prod: product Group folder
		page: dynamic page where the dynpack belongs
		configId: the dynpack xml filename without extension
	The code cleans the session, fills the prodEnv bean and redirects to the DynDispatcher 
	with the state = "start".
	The language is set to default (en_US).
--%>

<%@ page import="trisoftdp.core.ProdEnvBean, trisoftdp.core.DynException" %>
<%
	String configId=request.getParameter("configId");
	String prod=(String) request.getParameter("prod");
	String stranitsa =request.getParameter("page");
	if(prod == null || configId == null || stranitsa == null) 
		throw new ServletException("configId or prod or page not specified");
	String bl = "en_US";
	if(session != null)
		session.invalidate();
	session = request.getSession(true);
	session.setAttribute("prod", prod);
	try {
		ProdEnvBean prodEnv = new ProdEnvBean(prod, bl);
		session.setAttribute("prodEnv", prodEnv);
	} catch (DynException e) {
		throw new ServletException("DynException: " + e.getMessage());
	}
	
	session.setAttribute("state", "start");
	response.sendRedirect(String.format("DynDispatcher?page=%s&prod=%s&configId=%s", stranitsa, prod, configId));
%>