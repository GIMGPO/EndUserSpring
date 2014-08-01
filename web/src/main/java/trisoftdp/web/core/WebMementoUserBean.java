package trisoftdp.web.core;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import trisoftdp.core.DynException;
import trisoftdp.core.MementoUserBean;

public class WebMementoUserBean extends MementoUserBean implements HttpSessionBindingListener {

	private static final long serialVersionUID = 1L;

	public void push(HttpServletRequest request) throws DynException {
		pushUserPack();
		pushLegend();
		if(request == null)
			return;
		requestParams = new HashMap<String,String[]>();
		Enumeration<String>pars = request.getParameterNames();
		String param;
		while(pars.hasMoreElements()) {
			param = pars.nextElement();
			requestParams.put(param, request.getParameterValues(param).clone());
		}
		requestParamsStack.push(requestParams);
	}

	public void valueBound(HttpSessionBindingEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void valueUnbound(HttpSessionBindingEvent evt) {
		// TODO Auto-generated method stub
		
	}
}
