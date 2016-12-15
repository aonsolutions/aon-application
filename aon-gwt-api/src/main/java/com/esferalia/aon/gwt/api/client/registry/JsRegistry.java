package com.esferalia.aon.gwt.api.client.registry;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;

public class JsRegistry extends JsObject {

	protected JsRegistry() {
	
	}
	
	public final native String getAlias() /*-{
		return this.alias;
	}-*/;

	public final native String getDocument() /*-{
		return this.document;
	}-*/;

}
