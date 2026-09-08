package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Element;

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
	
	private static native String getIsOfficeRaw()
	/*-{
	    var v = $wnd.localStorage.getItem("isOffice");
	    return v == null ? null : String(v);
	}-*/;

	public static boolean isOffice() {
	    return "true".equalsIgnoreCase(getIsOfficeRaw());
	}
	
	public static native int removeIsOffice()
	/*-{
		return $wnd.localStorage.removeItem("isOffice");
	}-*/;

	/**
	 * Get value of CSS variable. 
	 * <cssSelector> {
  	 *	--<cssVar>: <cssValue>;
	 *	} 	
	 * @param element
	 * @param cssVar without '--' prefix
	 * @return cssValue
	 */
	public static native String getCSSVariable(Element element, String cssVar) /*-{
		return $wnd.getComputedStyle(element).getPropertyValue('--' + cssVar);
	}-*/;


}
