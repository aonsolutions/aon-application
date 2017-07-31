package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsGithub extends JavaScriptObject {

	protected JsGithub() {}
	
	public final native Boolean isActive() /*-{
		return this.active;
	}-*/;
	
	public final native String getUsername() /*-{
		return this.username;
	}-*/;

	public final native String getRepository() /*-{
		return this.repository;
	}-*/;
	
	public final native String getToken() /*-{
		return this.token;
	}-*/;

}
