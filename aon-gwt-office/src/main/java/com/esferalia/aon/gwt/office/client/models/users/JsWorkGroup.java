package com.esferalia.aon.gwt.office.client.models.users;

import com.google.gwt.core.client.JavaScriptObject;

public class JsWorkGroup extends JavaScriptObject {

	protected JsWorkGroup() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getDescription() /*-{
		return this.description;
	}-*/;
}
