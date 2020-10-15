package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public interface SaltraService {

	public static enum Parameter {
		CIF,
		NIF,
//		NAF,
		CCC,
		DATE,
		FILE, 
		USER,
		DOMAIN,
		REGIME,
		CONTRACT,
		PASSWORD,
	}
	
	public static class JsSaltraResults extends JavaScriptObject {
		protected JsSaltraResults() {
		}
		
		public final native Integer getEmployeeId() /*-{
			return this.employeeId;
		}-*/;
		
		public final native Integer getWorkplaceId() /*-{
			return this.workplaceId;
		}-*/;
	}

	public static final String SALTRA_URL = URL
			.encode(GWT.getModuleBaseURL() + "saltra");
	
	public static final String EMPLOYEE = "employee";
	public static final String REGISTER = "register";
	public static final String CERTIFICATE = "certificate";
	
	
	
}
