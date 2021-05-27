package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsComment extends JavaScriptObject {

	protected JsComment() {}
	
	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getBody() /*-{
		return this.body;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;
	
	public final native JsObject getSource() /*-{
		return this.source;
	}-*/;

	public final native Integer getSourceId() /*-{
		return this.source_id;
	}-*/;

	
	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;

	public final native String getUpdatedAt() /*-{
		return this.updated_at;
	}-*/;

}
