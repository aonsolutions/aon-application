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

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getEnterprise() /*-{
		return this.enterprise;
	}-*/;

}
