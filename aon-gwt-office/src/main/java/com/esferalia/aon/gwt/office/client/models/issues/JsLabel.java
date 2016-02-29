package com.esferalia.aon.gwt.office.client.models.issues;

import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JavaScriptObject;

public class JsLabel extends JavaScriptObject {

	protected JsLabel() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native byte getType() /*-{
		return this.type;
	}-*/;

	public final native String getColor() /*-{
		return this.color;
	}-*/;

	public final native int getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;

	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;

	public final native String getDeletedAt() /*-{
		return this.deleted_at;
	}-*/;

}
