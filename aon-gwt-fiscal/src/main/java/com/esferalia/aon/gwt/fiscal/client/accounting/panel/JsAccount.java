package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.google.gwt.core.client.JavaScriptObject;

public class JsAccount extends JavaScriptObject {
	
	protected JsAccount() {
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native int getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getCode() /*-{
		return this.code;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native String getAlias() /*-{
		return this.alias;
	}-*/;
	
	public final native int getLevel() /*-{
		return this.level;
	}-*/;
	
	public final native boolean isActive() /*-{
		return this.active;
	}-*/;
	
	public final native String getCostCenter() /*-{
		return this.costCenter;
	}-*/;
	
	public final native boolean hasRegistry() /*-{
	return this.hasRegistry;
}-*/;
}
