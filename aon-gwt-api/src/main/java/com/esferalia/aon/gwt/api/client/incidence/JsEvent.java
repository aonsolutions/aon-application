package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsEvent extends JavaScriptObject {

	protected JsEvent() {}
	
	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.actor;
	}-*/;

	public final native String getEvent() /*-{
		return this.event;
	}-*/;
	
	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;
}
