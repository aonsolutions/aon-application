package com.esferalia.aon.gwt.payroll.client;

public class Wnd {

	public static native String getToken()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? token : '';
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? '' : $wnd.getCurrentUser();
	}-*/;

	public static native String getCurrentDomainNameURL()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? $wnd.localStorage.getItem("aon_domain_name") : $wnd.getCurrentDomainNameURL();
	}-*/;


}
