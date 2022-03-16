package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.google.gwt.core.client.JavaScriptObject;

public class JsIRPFComputeInfo extends JavaScriptObject {
	
	protected JsIRPFComputeInfo() {
	}
	
	public final native String getKey() /*-{
		return this.key;
	}-*/;
	public final native String getValue() /*-{
		return this.value;
	}-*/;
	public final native String getExpression() /*-{
		return this.expression;
	}-*/;
	public final native String getPattern() /*-{
		return this.pattern;
	}-*/;
	public final native String getFormula() /*-{
		return this.formula;
	}-*/;
	public final native String getResult() /*-{
		return this.result;
	}-*/;
	public final native String[] getKeys() /*-{
		return this.keys;
	}-*/;
	public final native String[] getKeyValues() /*-{
		return this.keyValues;
	}-*/;
}
