package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUser extends JavaScriptObject {

	protected JsUser() {}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getLogin() /*-{
		if(this.login === undefined){
			return "Sin Asignar";
		}
		return this.login;
	}-*/;
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getEmail() /*-{
		return this.email;
	}-*/;
}
