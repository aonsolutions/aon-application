package com.esferalia.aon.gwt.api.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JSON<T extends JavaScriptObject> extends JavaScriptObject {

	protected JSON(){}
		
	public final native AonJsArray<T> getData() /*-{ return this.data; }-*/;

}
