package com.esferalia.aon.gwt.common.client;


import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;

public abstract class AONEntryPoint implements EntryPoint {


	public String getParameter(
			String parameterName) {
		return getParameter(GWT.getModuleName(), parameterName);
	}

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
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for (var j = 0; params && j < params.length; ++j) {
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
