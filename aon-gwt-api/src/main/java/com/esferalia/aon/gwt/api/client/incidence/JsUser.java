package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsUser extends JavaScriptObject {

	protected JsUser() {}

	public static final ProvidesKey<JsUser> PROVIDES_KEY = new ProvidesKey<JsUser>() {
		@Override
		public Object getKey(JsUser p) {
			return p == null ? null : p.getId();
		}
	};

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getLogin() /*-{
		if(this.login === undefined){
			return "Sin Asignar";
		}
		return this.login;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getEmail() /*-{
		return this.email;
	}-*/;
	
	public final native AonJsArray<JsUser> getWorkgroups() /*-{
		return this.workgroups;
	}-*/;
	
	public final native JsObject getStatus() /*-{
		return this.customer_status;
	}-*/;	
}
