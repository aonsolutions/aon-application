package com.esferalia.aon.gwt.api.client.finance;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.google.gwt.core.client.JavaScriptObject;

public class JsBoughtProduct extends JavaScriptObject {

	protected JsBoughtProduct() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native Double getQuantity() /*-{
		return this.quantity;
	}-*/;
	
	public final native Double getPrice() /*-{
		return this.price;
	}-*/;
	
	public final native Double getDiscount() /*-{
		return this.discount;
	}-*/;

	public final native Double getDate() /*-{
		return this.creation_date;
	}-*/;
	
	public final native Double getCode() /*-{
		return this.code;
	}-*/;
	
	public final native Double getTotal() /*-{
		return this.total;
	}-*/;
	
	public final native AonJsArray<JsBoughtProduct> getArray() /*-{
		return this.array;
	}-*/;
}
