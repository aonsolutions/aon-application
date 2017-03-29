package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsSalesDetail extends JavaScriptObject {

	protected JsSalesDetail() {}	public static final ProvidesKey<JsSalesDetail> PROVIDES_KEY = new ProvidesKey<JsSalesDetail>() {
		@Override
		public Object getKey(JsSalesDetail salesDetail) {
			return salesDetail == null ? null : salesDetail.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getSales() /*-{
		return this.sales;
	}-*/;
	
	public final native Integer getLine() /*-{
		return this.line;
	}-*/;
	
	public final native Integer getItem() /*-{
		return this.item;
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
	
	public final native String getDiscountExpr() /*-{
		return this.discount_expr;
	}-*/;
	
	public final native Double getTaxes() /*-{
		return this.taxes;
	}-*/;
	
	public final native JsObject getStatus() /*-{
		return this.status;
	}-*/;
	
	public final native Double getDelivered() /*-{
		return this.delivered;
	}-*/;
	
	
}
