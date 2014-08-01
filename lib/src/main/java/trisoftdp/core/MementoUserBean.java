package trisoftdp.core;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class MementoUserBean extends UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Stack<DynamicPublishingPackage> userPackStack = new Stack<DynamicPublishingPackage>(); 
	private Stack<Map<String,String>> legendStack = new Stack<Map<String,String>>();
	protected Stack<Map<String,String[]>> requestParamsStack = new Stack<Map<String,String[]>>();
	protected Map<String,String[]> requestParams = new HashMap<String,String[]>();

	public MementoUserBean() { super(); } 
	
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

}
