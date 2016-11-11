package com.esferalia.aon.gwt.dump.client;

import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;

public class MainEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		ensureGwtSelector();
		
		DSIImportForm dsiImportForm = new DSIImportForm();
		dsiImportForm.onModuleLoad();
	}
	
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for (var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (StringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}


}
