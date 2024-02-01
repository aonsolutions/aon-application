package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;

public interface IvlService {

	public enum Parameter {
	    	CCC,
		FILE, 
		USER, 
		DOMAIN, 
	    	END_DATE,
	    	START_DATE
	}
	
	
	public static final String IVL_URL = URL
			.encode(GWT.getModuleBaseURL() + "ivl");
	
	public static final DateTimeFormat DATE_FORMAT = 
		DateTimeFormat.getFormat("dd/MM/yyyy"); 
	
	
}
