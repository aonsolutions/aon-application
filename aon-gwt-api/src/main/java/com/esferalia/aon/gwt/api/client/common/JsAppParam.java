package com.esferalia.aon.gwt.api.client.common;

import com.google.gwt.core.client.JavaScriptObject;

public class JsAppParam extends JavaScriptObject {

	protected JsAppParam() {}
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getValue() /*-{
		return this.value;
	}-*/;


}
