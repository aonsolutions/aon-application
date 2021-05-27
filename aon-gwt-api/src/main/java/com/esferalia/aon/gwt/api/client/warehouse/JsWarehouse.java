package com.esferalia.aon.gwt.api.client.warehouse;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsWarehouse extends JavaScriptObject {

	protected JsWarehouse() {}	public static final ProvidesKey<JsWarehouse> PROVIDES_KEY = new ProvidesKey<JsWarehouse>() {
		@Override
		public Object getKey(JsWarehouse warehouse) {
			return warehouse == null ? null : warehouse.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native Integer getWorkplace() /*-{
		return this.workplace;
	}-*/;

	public final native Integer getDepartment() /*-{
		return this.department;
	}-*/;
	
	public final native Boolean isActive() /*-{
		return this.active;
	}-*/;
}