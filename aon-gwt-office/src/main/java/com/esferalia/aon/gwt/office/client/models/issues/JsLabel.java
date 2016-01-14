package com.esferalia.aon.gwt.office.client.models.issues;

import com.google.gwt.core.client.JavaScriptObject;

public class JsLabel extends JavaScriptObject {

	protected JsLabel() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getColor() /*-{
		return this.color;
	}-*/;

	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;	
}
