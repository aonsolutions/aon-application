package com.esferalia.aon.gwt.treasury.client;

import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.Window;

public class MainEntryPoint implements EntryPoint {
	
	interface CodeMirrorResources extends ClientBundle {
		@NotStrict
		@Source("codemirror.css")
		CssResource css();
	}

	@Override
	public void onModuleLoad() {
		Window.alert("onModuleLoad");
		ensureGwtSelector();
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);

		if (entryPoint.equalsIgnoreCase(Constants.MAIN_CUSTOMER_FEE_ENTRY_POINT)) {
			runAsync(MainCustomerFee.class, new MainCustomerFee());
		}
		
	}
	

	public static void runAsync (Class<?> name,EntryPoint entryPoint) {
		if (name == MainCustomerFee.class ) {
			GWT.runAsync(MainCustomerFee.class, new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					entryPoint.onModuleLoad();;
				}
				
				@Override
				public void onFailure(Throwable reason) {
	                Window.alert("Error al cargar");
				}
			});
		} else {
			Window.alert("Modulo desconcido '" + name +"'");
		}
	}

	public static void runAsync (Class<?> name, Runnable runnable) {
		GWT.runAsync(name, new RunAsyncCallback() {
			
			@Override
			public void onSuccess() {
				runnable.run();
			}
			
			@Override
			public void onFailure(Throwable reason) {
                Window.alert("Error al cargar");
			}
		});
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
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}
	
	public static native boolean supportWebComponents ()/*-{
		try {
			var object = document.createComment("")
			var nativePrototype = Object.getPrototypeOf(object);
			var descr = Object.getOwnPropertyDescriptor(Element.prototype, "classList");
			Object.defineProperty(HTMLElement.prototype, "classList", descr);
			return true;
		} catch(ex) {
			return false;
		}
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;


}
