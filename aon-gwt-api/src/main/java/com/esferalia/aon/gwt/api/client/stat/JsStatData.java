package com.esferalia.aon.gwt.api.client.stat;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStatData extends JavaScriptObject {

	protected JsStatData() {}
	
	public final native String getRow() /*-{
		return this.row;
	}-*/;

	public final native String getColumn() /*-{
		return this.column;
	}-*/;
	
	public final native Double getQuantity() /*-{
		return this.quantity;
	}-*/;

}
