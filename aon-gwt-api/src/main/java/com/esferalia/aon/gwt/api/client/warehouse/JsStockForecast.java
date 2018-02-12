package com.esferalia.aon.gwt.api.client.warehouse;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsStockForecast extends JavaScriptObject {

	protected JsStockForecast() {
	}

	public static final ProvidesKey<JsStockForecast> PROVIDES_KEY = new ProvidesKey<JsStockForecast>() {
		@Override
		public Object getKey(JsStockForecast js) {
			return js == null ? null : js.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native String getProductName() /*-{
												return this.product_name;
												}-*/;

	public final native Double getQuantity() /*-{
												return this.quantity;
												}-*/;

	public final native Double getDailyQuantity() /*-{
													return this.daily_quantity;
													}-*/;
	
	public final native Double getAccumulation() /*-{
													return this.accumulation;
													}-*/;

	public final native Double getStock() /*-{
											return this.stock;
											}-*/;

	public final native Double getPendingPurchases() /*-{
												return this.pending_purchases;
												}-*/;

	public final native Double getPendingSales() /*-{
												return this.pending_sales;
												}-*/;

	public final native Double getProposal() /*-{
												return this.proposal;
												}-*/;

}
