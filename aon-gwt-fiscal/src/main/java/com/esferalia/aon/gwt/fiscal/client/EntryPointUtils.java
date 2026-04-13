package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.Occam;

public class EntryPointUtils {
	
	public static  Occam getOccam() {
		return new Occam()
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser());
	}
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	public static native int getCustomer()
	/*-{
		return $wnd.localStorage.getItem("customer");
	}-*/;
	
	public static native int removeCustomer()
	/*-{
		return $wnd.localStorage.removeItem("customer");
	}-*/;
	
	public static String getCurrentUser() {
		return getCurrentUserJs();
	}
	
	public static native String getCurrentUserJs()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	public static native int getOfficeDomain()
	/*-{
		return $wnd.localStorage.getItem("officeDomain");
	}-*/;
	
	public static native int removeOfficeDomain()
	/*-{
		return $wnd.localStorage.removeItem("officeDomain");
	}-*/;
	
	public static native boolean isSig()
	/*-{
		return $wnd.localStorage.getItem("isSig");
	}-*/;
	
	public static native int getBookingDomainId()
	/*-{
		return $wnd.localStorage.getItem("booking_domain_id");
	}-*/;
	
	public static native String getBookingDomainName()
	/*-{
		return $wnd.localStorage.getItem("booking_domain_name");
	}-*/;
	
	public static native int removeBookingDomainId()
	/*-{
		return $wnd.localStorage.removeItem("booking_domain_id");
	}-*/;
	
	public static native String removeBookingDomainName()
	/*-{
		return $wnd.localStorage.removeItem("booking_domain_name");
	}-*/;
	
	public static native String getRegistrySource()
	/*-{
		return $wnd.localStorage.getItem("registrySource");
	}-*/;
	
	public static native int removeRegistrySource()
	/*-{
		return $wnd.localStorage.removeItem("registrySource");
	}-*/;
	
	/**
	 * Fetches a parameter passed to the module's nocache script.
	 * 
	 * @param moduleName
	 *            the module's name.
	 * @param parameterName
	 *            the name of the parameter to fetch.
	 * @return the value of the parameter, or <code>null</code> if it was not
	 *         found.
	 */
	public static native String getParameter(String moduleName, String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for ( var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for ( var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

}
