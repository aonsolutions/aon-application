package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsLabel extends JavaScriptObject {

	protected JsLabel() {}
	
	public final native String getId() /*-{
		return this.url;
	}-*/;
	
	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native String getColor() /*-{
		return this.color;
	}-*/;

	public final native String getName() /*-{
		if(this.name === undefined){
			return "Sin Asignar";
		}
		return this.name;
	}-*/;

}
