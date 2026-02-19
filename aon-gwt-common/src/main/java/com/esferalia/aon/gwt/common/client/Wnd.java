package com.esferalia.aon.gwt.common.client;

import java.util.Optional;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

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

	public static native void consoleLog(String msg) /*-{
		console.log(msg);
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

	public static Optional<String> getCSSOptionalVariable(Widget widget, String cssVar) {
		String value = getCSSVariable(widget.getElement() , cssVar);
		return value != null && !value.trim().isEmpty() ? Optional.of(value.trim()) : Optional.empty();
	}
}
