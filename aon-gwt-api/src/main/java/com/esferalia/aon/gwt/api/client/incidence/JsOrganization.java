package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsOrganization extends JavaScriptObject {

	protected JsOrganization() {}
	
	public final native String getLogin() /*-{
		return this.login;
	}-*/;

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String geturl() /*-{
		return this.url;
	}-*/;

}
