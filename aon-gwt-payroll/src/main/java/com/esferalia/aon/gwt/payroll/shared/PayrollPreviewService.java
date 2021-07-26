package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public interface PayrollPreviewService {
	
	public static enum Parameter {
		CONTRACTS,
		FILE_NAME,
		DOMAIN_NAME,
	}

	public static final String PREVIEW_URL = URL.encode(GWT.getModuleBaseURL() + "preview_payroll");
	
}
