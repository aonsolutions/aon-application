package com.esferalia.aon.gwt.api.client.commercial;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCommission extends JavaScriptObject {

	public static final ProvidesKey<JsCommission> PROVIDES_KEY = new ProvidesKey<JsCommission>() {
		@Override
		public Object getKey(JsCommission commission) {
			return commission == null ? null : commission.getId();
		}
	};
	
	protected JsCommission() {}
	
	public final native Integer getId() /*-{
		return this.type;
	}-*/;
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getDate() /*-{
		return this.date;
	}-*/;
	
	public final native String getSeller() /*-{
		return this.seller;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native String getProduct() /*-{
		return this.product;
	}-*/;
	
	public final native String getQuantity() /*-{
		return this.quantity;
	}-*/;
	
	public final native String getPrice() /*-{
		return this.price;
	}-*/;
	
	public final native String getDiscount() /*-{
		return this.discount;
	}-*/;
	
	public final native Double getBase() /*-{
		return this.base;
	}-*/;
	
	public final native Double getPercentage() /*-{
		return this.percentage;
	}-*/;

	public final native Double getAmount() /*-{
		return this.amount;
	}-*/;
	
	public final native String getStatus() /*-{
		return this.status;
	}-*/;
	
	

	
	


	
	
}
