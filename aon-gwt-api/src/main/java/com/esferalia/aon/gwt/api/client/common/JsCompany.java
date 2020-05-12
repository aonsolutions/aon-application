package com.esferalia.aon.gwt.api.client.common;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCompany extends JavaScriptObject {

	protected JsCompany() {}	
	
	public static final ProvidesKey<JsCompany> PROVIDES_KEY = new ProvidesKey<JsCompany>() {
		@Override
		public Object getKey(JsCompany p) {
			return p == null ? null : p.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native String getDocument() /*-{
		return this.document;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;	
	}-*/;
	
	public final native JsObject getScope() /*-{
		return this.scope;	
	}-*/;

}
