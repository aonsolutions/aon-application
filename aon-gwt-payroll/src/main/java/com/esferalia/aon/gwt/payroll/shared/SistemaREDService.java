package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public interface SistemaREDService {

	public static enum Parameter {
		ID, 
		CIF,
//		NIF,
		NAF,
		CCC,
		DATE,
		FILE, 
		USER,
		COUNT,
		DOMAIN,
		REGIME,
		CONTRACT,
		PASSWORD,
	}
	
	public static class JsSistemaREDResults extends JavaScriptObject {
		protected JsSistemaREDResults() {
		}
		
		public final native int getEmployeeId() /*-{
			return this.employeeId;
		}-*/;
		
		public final native int getWorkplaceId() /*-{
			return this.workplaceId;
		}-*/;
	}

	public static final String SISTEMA_RED_URL = URL
			.encode(GWT.getModuleBaseURL() + "seg-social");
	
	public static final String EMPLOYEE = "employee";
	public static final String REGISTER = "register";
	public static final String EMPLOYEES = "employees";
	public static final String CERTIFICATE = "certificate";
	public static final String UP2DATE_REPORT = "up2date_report";
	public static final String UP2DATE_CCC_REPORT = "up2date_ccc_report";
	
	
	
}
