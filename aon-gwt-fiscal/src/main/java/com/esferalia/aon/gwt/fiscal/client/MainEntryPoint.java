package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.fiscal.client.mod180.Model180;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class MainEntryPoint implements EntryPoint {

	private static final String ENTRY_POINT_PARAM = "entryPoint";
	private static final String FS_MOD190_ENTRY_POINT = "Model190";
	private static final String FS_MOD193_ENTRY_POINT = "Model193";
	private static final String FS_MOD180_ENTRY_POINT = "Model180";	
	private static final String FS_MOD184_ENTRY_POINT = "Model184";
	private static final String FS_MOD390_ENTRY_POINT = "Model390";
	private static final String FS_MOD200_ENTRY_POINT = "Model200";

	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);
		if ( entryPoint.equalsIgnoreCase(FS_MOD190_ENTRY_POINT)) {
			Model190 model190 = new Model190();
			model190.onModuleLoad();
		}
		if ( entryPoint.equalsIgnoreCase(FS_MOD193_ENTRY_POINT)) {
			Model193 model193 = new Model193();
			model193.onModuleLoad();
		}
		if ( entryPoint.equalsIgnoreCase(FS_MOD180_ENTRY_POINT)) {
			Model180 model180 = new Model180();
			model180.onModuleLoad();
		}
		if ( entryPoint.equalsIgnoreCase(FS_MOD184_ENTRY_POINT)) {
			Model184 model184 = new Model184();
			model184.onModuleLoad();
		}
		if ( entryPoint.equalsIgnoreCase(FS_MOD390_ENTRY_POINT)) {
			Model390 model390 = new Model390();
			model390.onModuleLoad();
		}
		if ( entryPoint.equalsIgnoreCase(FS_MOD200_ENTRY_POINT)) {
			Model200 model200 = new Model200();
			model200.onModuleLoad();
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
