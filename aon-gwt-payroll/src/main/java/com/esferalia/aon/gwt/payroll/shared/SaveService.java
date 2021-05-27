package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public interface SaveService {
	
	public static final String SAVE_URL = URL
			.encode(GWT.getModuleBaseURL() + "save");
	
	
	

}
