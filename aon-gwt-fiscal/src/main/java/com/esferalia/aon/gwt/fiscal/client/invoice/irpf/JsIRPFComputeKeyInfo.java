package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.google.gwt.core.client.JavaScriptObject;

public class JsIRPFComputeKeyInfo extends JavaScriptObject {
	
	protected JsIRPFComputeKeyInfo() {
	}
	
	public final native String[] getMessages() /*-{
		return this.messages;
	}-*/;
}
