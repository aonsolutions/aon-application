package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsSize extends JavaScriptObject {

	protected JsSize() {}
	
	public final native int getOpen() /*-{
		return this.open;
	}-*/;
	
	public final native int getClosed() /*-{
		return this.closed;
	}-*/;

	public final native int getDeleted() /*-{
		return this.deleted;
	}-*/;


}
