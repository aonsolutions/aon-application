package com.esferalia.aon.gwt.office.client.models.repos;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRegistry extends JavaScriptObject {

	protected JsRegistry() {
	}

	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getDocument() /*-{
		return this.document;
	}-*/;

}
