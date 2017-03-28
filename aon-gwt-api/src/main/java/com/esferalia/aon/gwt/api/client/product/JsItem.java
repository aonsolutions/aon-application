package com.esferalia.aon.gwt.api.client.product;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsItem extends JavaScriptObject {

	protected JsItem() {}	public static final ProvidesKey<JsItem> PROVIDES_KEY = new ProvidesKey<JsItem>() {
		@Override
		public Object getKey(JsItem item) {
			return item == null ? null : item.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;

	public final native Integer getProductId() /*-{
		return this.product_id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getCode() /*-{
		return this.code;
	}-*/;
	
}