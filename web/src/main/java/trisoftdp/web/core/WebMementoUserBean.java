package trisoftdp.web.core;

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.core.UserBean;

public class WebMementoUserBean extends UserBean implements HttpSessionBindingListener {

	private static final long serialVersionUID = 1L;
	private Stack<DynamicPublishingPackage> userPackStack = new Stack<DynamicPublishingPackage>(); 
	private Stack<Map<String,String>> legendStack = new Stack<Map<String,String>>();
	protected Stack<Map<String,String[]>> requestParamsStack = new Stack<Map<String,String[]>>();
	protected Map<String,String[]> requestParams = new HashMap<String,String[]>();

	public WebMementoUserBean() { super(); } 
	
	public UserBean extractUserBean() {
		UserBean ub = new UserBean();
		ub.setUserEmail(getUserEmail());
		ub.setConfigId(getConfigId());
		ub.setProfileAliases(getProfileAliases());
		ub.setPubLegend(getPubLegend());
		ub.setDynPack(getDynPack());
		ub.setUserPack(getUserPack());
		return ub;
	}
	
	public boolean getIsStackEmpty() { return userPackStack.isEmpty();	}
	
	public int getStackSize() { return userPackStack.size();	}
	
	public void clearStack() {	
		userPackStack.clear();
		legendStack.clear();
		requestParamsStack.clear();
	}
	
	public String getParameter(String name) {
		return (requestParams.containsKey(name))? requestParams.get(name)[0] : null ;
	}
	
	public String[] getParameterValues(String name) {
		return requestParams.get(name);
	}
	
	public void pop() throws DynException {
		popUserPack();
		popLegend();
		requestParams = requestParamsStack.pop();
	}
		
	private void popUserPack() throws DynException {
		try {
			setUserPack(userPackStack.pop());
		} catch (EmptyStackException e) {
			throw new DynException("EmptyStackException: " + e.getMessage());
		}
	}
	
	protected void pushUserPack() throws DynException {
		DynamicPublishingPackage up;
		try {
			up = getUserPack();
			if(up == null)
				return; 
			userPackStack.push(up.clone());
		} catch (CloneNotSupportedException e) {
			throw new DynException("CloneNotSupportedException: " + e.getMessage());
		}
	}
		
	private void popLegend() throws DynException {
		try {
			setPubLegend(legendStack.pop());
		} catch (EmptyStackException e) {
			throw new DynException("EmptyStackException: " + e.getMessage());
		}
	}
	
	protected void pushLegend() {
			if(getPubLegend() == null)
				return; 
			Map<String,String> legend = new HashMap<String,String>();
			for(Entry<String, String> entry: getPubLegend().entrySet())
					legend.put(entry.getKey(), entry.getValue());
			legendStack.push(legend);
	}
	@Override 
	public void clean() {
		super.clean();
		userPackStack.clear();
	}
	
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
