package com.esferalia.aon.gwt.stat.client;

import com.esferalia.aon.gwt.stat.client.panel.StatControlPanel;
import com.google.gwt.core.client.EntryPoint;

public class MainEntryPoint implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	//
	//    ================================================================== STATS
	//
	private static final String ST_STATS_ENTRY_POINT = "StatControlPanel";
	
	@Override
	public void onModuleLoad() {
		
		/*String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);
		if ( entryPoint.equalsIgnoreCase(ST_STATS_ENTRY_POINT)) {
		*/
			new StatControlPanel().onModuleLoad();
		//}
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
