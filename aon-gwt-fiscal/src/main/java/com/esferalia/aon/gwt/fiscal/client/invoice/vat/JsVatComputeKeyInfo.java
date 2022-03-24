package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import com.google.gwt.core.client.JavaScriptObject;

public class JsVatComputeKeyInfo extends JavaScriptObject {
	
	protected JsVatComputeKeyInfo() {
	}
	
	public final native String[] getMessages() /*-{
		return this.messages;
	}-*/;
}
