package com.esferalia.aon.gwt.payroll.client;

public class Wnd {

	public static native boolean isSysAdmin()
	/*-{
		return $wnd.isSysAdmin();
	}-*/;

	public static native boolean isNewAONTheme()
	/*-{
		return $wnd.isNewAONTheme();
	}-*/;

	public static boolean isClassicAONTheme() {
		return !isNewAONTheme();
	}

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

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	public static native String getCurrentDomainNameURL()
	/*-{
		return $wnd.getCurrentDomainNameURL();
	}-*/;

}
