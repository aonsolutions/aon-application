package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;

public interface SistemaREDService {
	public static String DATE_FORMAT = "dd/MM/yyyy";

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
		START_DATE,
		END_DATE,
	}
	
	public static class JsSistemaREDProgess extends JavaScriptObject {
		protected JsSistemaREDProgess() {
		}
		
		public final native int getTotal() /*-{
			return this.total;
		}-*/;
		
		public final native int getProgress() /*-{
			return this.progress;
		}-*/;

		public final native String getEmployeeName() /*-{
			return this.employeeName;
		}-*/;
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

	public static class JsSistemaREDEmployeeIT extends JavaScriptObject {
		protected JsSistemaREDEmployeeIT() {
		}
		
		public final native String getNaf() /*-{
			return this.naf;
		}-*/;
		
		public final native String getName() /*-{
			return this.name;
		}-*/;
		
		public final native JsArray<JsSistemaREDIT> getItsNotInAon() /*-{
			return this.itsNotInAon;
		}-*/;
		
		public final native JsArray<JsSistemaREDIT> getItsNotInTgss() /*-{
			return this.itsNotInTgss;
		}-*/;
		
		public final native JsArray<JsSistemaREDIT> getItsInBoth() /*-{
			return this.itsInBoth;
		}-*/;
		
		public final native JsArray<JsSistemaREDIT> getItsInTgss() /*-{
			return this.itsInTgss;
		}-*/;
		
	}

	public static class JsSistemaREDIT extends JavaScriptObject {
		
		protected JsSistemaREDIT() {
		}

		public final native String getCcc() /*-{
			return this.ccc;
		}-*/;
		
		public final native String getNss() /*-{
			return this.nss;
		}-*/;
		
		public final native String getRegime() /*-{
			return this.regime;
		}-*/;
		
		public final native String getName() /*-{
			return this.name;
		}-*/;
		
		public final native String getDni() /*-{
			return this.dni;
		}-*/;
		
		public final native String getStartDate() /*-{
			return this.startDate;
		}-*/;
		
		public final native String getEndDate() /*-{
			return this.endDate;
		}-*/;
		
		public final native String getType() /*-{
			return this.type;
		}-*/;
		
		public final native String getId() /*-{
			return this.id;
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
	public static final String ENTERPRISE_IT_STATUS_TEST = "enterprise_it_status/test";
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
}
