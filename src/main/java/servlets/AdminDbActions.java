package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import trisoftdp.core.CoreConstants;
import trisoftdp.core.DbWebHelper;

/**
 * Servlet implementation class AdminDbActions
 */
public class AdminDbActions extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminDbActions() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String whatQuery = (String) request.getAttribute("query");
		String docId = (String) request.getAttribute("docId");
		long id = -1;
		int count = 0;
		String intentStatement = "";
		String resultStatement = "";
		if ("deleteAll".equals(whatQuery)) {
			CoreConstants.logger.info("whatQuery=deleteAll");
			count = DbWebHelper.cleanPubResults();
			intentStatement = "You asked to <b>Delete All Records</b>.";
			resultStatement = "<b>" + count + "</b> records have been deleted successfully.";
		} else if ("deleteNotGeneric".equals(whatQuery)) {
			count = DbWebHelper.cleanPubResults("generic");
			intentStatement = "You asked to <b>Delete All Records</b> but save cached static documents.";
			resultStatement = "<b>" + count + "</b> records are deleted successfully.";
		} else if ("deleteById".equals(whatQuery)) {
			try {
				id = Long.parseLong(docId);
			} catch(Exception e) {}
			if(id < 0) {
				request.getSession().setAttribute("warning","Please enter valid id");
				response.sendRedirect("index.jsp?admAct=dbActions");
			}
			count = DbWebHelper.deletePubResultsEntry(id);
			intentStatement = "You asked to <b>delete just one record</b>.";
			if (count < 0)
				resultStatement = "Well, we could not do it: the <b>result_id = " + docId + "</b> does not exist!";
			else 
				resultStatement = "The record with <b>result_id = " + docId + "</b> is deleted successfully.";
		} else {
			resultStatement = "I do not know what am I doing here...";
		} 
		request.getSession().setAttribute("intentStatement", intentStatement);
		request.getSession().setAttribute("resultStatement", resultStatement);
		response.sendRedirect("index.jsp?formSubmitted=dbAct");
	}

}
