package com.code.aon.ui.audit;

import java.util.List;

public interface IOption {

	String getAction();
	
	void setAction(String action);
	
	String getDescription();
	
	String getViewId();
	
	void setViewId(String viewId);
	
	List<ActionSource> getActionSources();
	
	boolean isRendered();
	
	String getRendered();

	void setRendered(String rendered);
	
}
