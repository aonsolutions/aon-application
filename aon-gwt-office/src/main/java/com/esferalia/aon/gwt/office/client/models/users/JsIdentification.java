package com.esferalia.aon.gwt.office.client.models.users;

import com.google.gwt.core.client.JavaScriptObject;

public class JsIdentification extends JavaScriptObject {

	protected JsIdentification() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getDocument() /*-{
		return this.document;
	}-*/;

	public final native String getAlias() /*-{
		return this.alias;
	}-*/;

	public final native String getValue() /*-{
		return this.value;
	}-*/;
}
