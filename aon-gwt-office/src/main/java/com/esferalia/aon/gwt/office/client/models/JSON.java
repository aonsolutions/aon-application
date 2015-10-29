package com.esferalia.aon.gwt.office.client.models;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSON<T extends JavaScriptObject> extends JavaScriptObject {
	
	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JSON(){}
	
	public final native boolean isNotFound() /*-{ return this.message == "Not Found"; }-*/;
	
	public final native JsArray<T> getData() /*-{ return this.data; }-*/;

}
