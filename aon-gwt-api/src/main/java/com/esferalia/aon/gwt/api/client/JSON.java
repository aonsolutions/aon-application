package com.esferalia.aon.gwt.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSON<T extends JavaScriptObject> extends JavaScriptObject {

	protected JSON(){}
		
	public final native JsArray<T> getData() /*-{ return this.data; }-*/;

}
