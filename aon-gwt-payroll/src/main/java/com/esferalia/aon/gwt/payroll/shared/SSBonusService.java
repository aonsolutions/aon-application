package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public interface SSBonusService {

	public static enum Parameter {
		FILE, 
		USER,
		DOMAIN
	}
	
	public static class JsSSBonusResults extends JavaScriptObject {
		protected JsSSBonusResults() {
		}
		
		public final native int getEmployeeId() /*-{
			return this.employeeId;
		}-*/;
		
	}

	public static final String SS_BONUS_URL = URL
			.encode(GWT.getModuleBaseURL() + "ss-bonus");
	
	public static final String IDC = "idc";
	
	
	
}
