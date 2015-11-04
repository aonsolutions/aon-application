package com.esferalia.aon.gwt.office.client.models.users;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUser extends JavaScriptObject {

	protected JsUser() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getLogin() /*-{
		return this.login;
	}-*/;

	public final native String getGravatarId() /*-{
		return this.gravatar_id;
	}-*/;

	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native String getAvatarUrl() /*-{
		return this.avatar_url;
	}-*/;

	public final native String getUserId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

}
