package com.esferalia.aon.gwt.api.client.seres;

import com.google.gwt.core.client.JavaScriptObject;

public class JsSummary extends JavaScriptObject {

	protected JsSummary() {}
	
	public final native String getLabel() /*-{
		return this.label;
	}-*/;

	public final native Integer getQuantity() /*-{
		return this.quantity;
	}-*/;
	
	public final native Integer getPending() /*-{
		return this.pending;
	}-*/;
	
	public final native Integer getError() /*-{
		return this.error;
	}-*/;

}
