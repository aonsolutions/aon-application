package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public interface SaltraService {

	public static enum Parameter {
		CIF,
		FILE, 
		USER,
		DOMAIN,
		PASSWORD
	}
	

	public static final String SALTRA_URL = URL
			.encode(GWT.getModuleBaseURL() + "saltra");
	
	public static final String CERTIFICATE = "certificate";
	
	
	
}
