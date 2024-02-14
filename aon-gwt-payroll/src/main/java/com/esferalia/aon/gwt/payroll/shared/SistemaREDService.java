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

		public final native String getEmployeeName() /*-{
			return this.employeeName;
		}-*/;
	}

	public static class JsSistemaREDCCC extends JavaScriptObject {
		protected JsSistemaREDCCC() {
		}
		
		public final native String getCCC() /*-{
			return this.province + this.number;
		}-*/;

		public final native String getRegime() /*-{
			return this.regime;
		}-*/;
		
		public final native String getProvince() /*-{
			return this.province;
		}-*/;

		public final native String getNumber() /*-{
			return this.number;
		}-*/;
		
		public final native String getEnterpriseName() /*-{
			return this.enterpriseName;
		}-*/;

		public final native String getType() /*-{
			return this.type;
		}-*/;
		
		public final native String getHashCode() /*-{
			return this.regime + this.province + this.number;
		}-*/;
		
	}

	public static final String SISTEMA_RED_URL = URL
			.encode(GWT.getModuleBaseURL() + "seg-social");
	
	public static final String CALCS = "calcs";
	public static final String EMPLOYEE = "employee";
	public static final String REGISTER = "register";
	public static final String EMPLOYEES = "employees";
	public static final String CERTIFICATE = "certificate";
	public static final String ASSIGNED_CCCS = "assigned_cccs";
	public static final String IDC_CCC_REPORT = "idc_ccc_report";
	public static final String UP2DATE_REPORT = "up2date_report";
	public static final String UP2DATE_CCC_REPORT = "up2date_ccc_report";
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
}
