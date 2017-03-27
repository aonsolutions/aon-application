package com.esferalia.aon.gwt.api.client.product;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsProduct extends JavaScriptObject {

	protected JsProduct() {}	public static final ProvidesKey<JsProduct> PROVIDES_KEY = new ProvidesKey<JsProduct>() {
		@Override
		public Object getKey(JsProduct p) {
			return p == null ? null : p.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getCode() /*-{
		return this.code;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
}