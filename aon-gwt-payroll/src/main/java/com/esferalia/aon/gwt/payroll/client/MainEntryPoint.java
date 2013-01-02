package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class MainEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {

		String entryPoint = getParameter(GWT.getModuleName(), "entryPoint");

		
		if ( entryPoint.equalsIgnoreCase("EnterpriseSite")) {
			EnterpriseSite enterpriseSite = new EnterpriseSite();
			enterpriseSite.onModuleLoad();
		}
		
		else if ( entryPoint.equalsIgnoreCase("EmployeeTree")) {
			EmployeeTree employeeTree = new EmployeeTree();
			employeeTree.onModuleLoad();
		}

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
		for ( var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var parameters = scripts[i].src.match(/\w+=\w+/g);
				for ( var j = 0; j < parameters.length; ++j) {
					var keyvalue = parameters[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

}
