package com.esferalia.aon.gwt.office.client.models.repos;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRMedia extends JavaScriptObject {

	protected JsRMedia() {
	}

	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native int getDomain() /*-{
		return this.domain;
	}-*/;

	public final native JsRegistry getRegistry() /*-{
		return this.registry;
	}-*/;

	public final native byte getMedia() /*-{
		return this.media;
	}-*/;

	public final native String getValue() /*-{
		return this.value;
	}-*/;

	public final native String getComment() /*-{
		return this.comment;
	}-*/;

}
