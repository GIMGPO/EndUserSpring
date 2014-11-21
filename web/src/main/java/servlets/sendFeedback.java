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
import trisoftdp.web.core.WebMementoUserBean;
import trisoftdp.core.ProdEnvBean;

/**
 * Servlet implementation class sendFeedback
 */
public class sendFeedback extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public sendFeedback() {
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
		//To get the submitted feedback in UTF-8 encoding (otherwise it will not show right).
		if(request.getCharacterEncoding() == null)
			   request.setCharacterEncoding("UTF-8");
		ProdEnvBean prodEnv = (ProdEnvBean) request.getSession().getAttribute("prodEnv");  
		WebMementoUserBean user = (WebMementoUserBean) request.getSession().getAttribute("user");
		if (user.getUserEmail()==null && request.getParameter("userEmail") !=null)
			user.setUserEmail(request.getParameter("userEmail"));
		Map<String,String> appStringsMap = new HashMap<String,String>();
		Locale l = (Locale) response.getLocale();
		CoreConstants.populateMap(appStringsMap, ResourceBundle.getBundle("appStr", l));
		String[] recs = {prodEnv.getProdSupportEmail()}; 
		StringBuilder buf = new StringBuilder();
		String rating = (request.getParameter("rating")!=null)?request.getParameter("rating"):"Not provided";
		String problemsolved = (request.getParameter("problemsolved")!=null)?request.getParameter("problemsolved"):"Not provided";
		String txtFeedback= (request.getParameter("txtFeedback")!=CoreConstants.appStringsMap.get("feedback.textbox.text"))?request.getParameter("txtFeedback"):"Not provided";
		buf.append("<html><head><style>td {font-family: Verdana; font-size: 12px; line-height: 16px; padding-right:4px;vertical-align:top;}</style></head><body>\n");
		buf.append("<div style = 'font-family: Arial; font-size: 15px; font-weight: bold; color:#095a90; display:block; border-bottom: 2px solid #095a90; padding-bottom:10px; margin:20px 0 20px 0;'>\n");
		buf.append(CoreConstants.appStringsMap.get("feedback.email.heading"));
		buf.append("</div>\n");
		buf.append("<table>");
		buf.append("<tr><td><b>" + CoreConstants.appStringsMap.get("feedback.email.rating") + "</b></td><td>" + rating + "</td></tr>\n");
		buf.append("<tr><td><b>" + CoreConstants.appStringsMap.get("feedback.email.message") + "</b></td><td>" + txtFeedback + "</td></tr>\n");
		buf.append("<tr><td><b>" + CoreConstants.appStringsMap.get("feedback.email.problemsolved") + "</b></td><td>" + problemsolved + "</td></tr>\n");
		buf.append(DynPubNotifications.getUserPackDetails(user.getUserEmail(), user, appStringsMap));
		buf.append("</table>");
		buf.append("<div style = 'font-family: Verdana; font-size: 9px; color:gray; display:block; border-top: 1px solid gray; padding-top:5px; margin:20px 0 20px 0; text-align:right;'>\n");
		buf.append("&copy;" + CoreConstants.appStringsMap.get("email.copyright") + "\n");
		buf.append("</div>\n");
		buf.append("</body></html>");
		try {
			DynMail.postMail(recs, CoreConstants.appStringsMap.get("feedback.email.subject") + user.getUserPack().packageName + " " + user.getUserPack().productRelease, buf.toString(), prodEnv.getProdSupportEmail(), "text/html; charset=utf-8");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.getSession().setAttribute("feedback", "complete");
		response.sendRedirect("DynDispatcher");
	}

}
