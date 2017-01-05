package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsObject extends JavaScriptObject {

	protected JsObject() {}
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getDate() /*-{
		return this.date;
	}-*/;

}
