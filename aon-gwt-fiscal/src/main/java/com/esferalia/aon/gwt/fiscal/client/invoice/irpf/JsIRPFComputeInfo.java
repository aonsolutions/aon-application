package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.google.gwt.core.client.JavaScriptObject;

public class JsIRPFComputeInfo extends JavaScriptObject {
	
	protected JsIRPFComputeInfo() {
	}
	
	public final native String getExpression() /*-{
		return this.expression;
	}-*/;
	public final native String[] getPatternItems() /*-{
		return this.pattern;
	}-*/;
	
	public final native String getFormula() /*-{
		return this.formula;
	}-*/;
	public final native String getResult() /*-{
		return this.result;
	}-*/;
	public final native Double getValue() /*-{
		return this.formula;
	}-*/;
	public final native String[] getKeys() /*-{
		return this.keys;
	}-*/;
}
