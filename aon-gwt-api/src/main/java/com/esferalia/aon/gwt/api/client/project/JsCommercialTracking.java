package com.esferalia.aon.gwt.api.client.project;

import com.esferalia.aon.gwt.api.client.registry.JsRegistry;
import com.google.gwt.core.client.JavaScriptObject;

public class JsCommercialTracking extends JavaScriptObject{

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsCommercialTracking() {

	}

	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public final native int getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getDate() /*-{
		return this.date;
	}-*/;

	public final native JsRegistry getSeller() /*-{
		return this.seller;
	}-*/;
	
	public final native String getComment() /*-{
		return this.comment;
	}-*/;
	
}
