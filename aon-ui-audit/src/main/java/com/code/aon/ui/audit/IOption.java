package com.code.aon.ui.audit;

import java.util.List;

public interface IOption {

	String ID_PATTERN = "(id)";
	
	String VALUE_PATTERN = "(value)";	
	
	String getAction();
	
	void setAction(String action);
	
	String getId();

	void setId(String id);	
	
	String getDescription();
	
	String getViewId();
	
	void setViewId(String viewId);
	
	List<ActionSource> getActionSources();
	
	boolean isRendered();
	
	String getRendered();

	void setRendered(String rendered);
	
	String getXml();

	void setXml(String xml);
	
	String getXml( String prefix );
	
}
