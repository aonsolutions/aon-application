package com.esferalia.aon.gwt.api.client.finance;

import com.google.gwt.core.client.JavaScriptObject;

public class JsInvoice extends JavaScriptObject {

	protected JsInvoice() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getRegistry() /*-{
		return this.registry;
	}-*/;
	
	public final native String getReferenceCode() /*-{
		return this.reference_code;
	}-*/;

}
