package com.esferalia.aon.gwt.api.client.product;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsItemAddinfo extends JavaScriptObject {

	protected JsItemAddinfo() {}
	
	public static final ProvidesKey<JsItemAddinfo> PROVIDES_KEY = new ProvidesKey<JsItemAddinfo>() {
		@Override
		public Object getKey(JsItemAddinfo itemAddinfo) {
			return itemAddinfo == null ? null : itemAddinfo.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getProduct() /*-{
		return this.product;
	}-*/;
	
	public final native Integer getItem() /*-{
		return this.item;
	}-*/;
	
	public final native String getAttribute() /*-{
		return this.attribute;
	}-*/;
	
	public final native String getValue() /*-{
		return this.value;	
	}-*/;
	
	public final native Date getDate() /*-{
		return this.date;	
	}-*/;
}
