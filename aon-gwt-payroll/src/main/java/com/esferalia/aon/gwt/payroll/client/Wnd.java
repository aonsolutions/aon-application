package com.esferalia.aon.gwt.payroll.client;

public class Wnd {

	public static native String isAonSolutions()
	/*-{
		var newAon = $wnd.localStorage.getItem("aon_solutions");
		return newAon ? true : false;
	}-*/;
	
	public static native String getToken()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? token : '';
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;

	public static native String getCurrentDomainNameURL()
	/*-{
		return $wnd.getCurrentDomainNameURL();
	}-*/;

}
