package servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DynMail;
import trisoftdp.core.DynPubNotifications;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.MementoUserBean;
import trisoftdp.core.ProdEnvBean;

/**
 * Servlet implementation class sendFeedback
 */
public class DynPubMissingStaticContent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DynPubMissingStaticContent() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProdEnvBean prodEnv = (ProdEnvBean) request.getSession().getAttribute("prodEnv");  
		MementoUserBean user = (MementoUserBean) request.getSession().getAttribute("user"); 
		Map<String,String> appStringsMap = new HashMap<String,String>();
		Locale l = (Locale) response.getLocale();
		CoreConstants.populateMap(appStringsMap, ResourceBundle.getBundle("appStr", l));
		String userEmail = (String) request.getParameter("userEmail");
		String[] recs = {prodEnv.getProdSupportEmail()}; 
		StringBuilder buf = new StringBuilder();
		buf.append("<html><body>\n");
		buf.append("<div style = 'font-family: Arial; font-size: 15px; font-weight: bold; color:#095a90; display:block; border-bottom: 2px solid #095a90; padding-bottom:10px; margin:20px 0 20px 0;'>\n");
		buf.append("Static Mapping Content is missing");
		buf.append("</div>\n");
		buf.append(DynPubNotifications.getUserPackDetails(userEmail, user, appStringsMap));
		buf.append("<div style = 'font-family: Verdana; font-size: 9px; color:gray; display:block; border-top: 1px solid gray; padding-top:5px; margin:20px 0 20px 0; text-align:right;'>\n");
		buf.append("&copy;" + CoreConstants.appStringsMap.get("email.copyright") + "\n");
		buf.append("</div>\n");
		buf.append("</body></html>");
		try {
			DynMail.postMail(recs, "Static Content is Missing", buf.toString(), prodEnv.getProdSupportEmail(), "text/html; charset=utf-8");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.getSession().setAttribute("feedback", "complete");
		request.getSession().setAttribute("state", "finish");
		response.sendRedirect("DynDispatcher");
	}

}
