package com.esferalia.aon.gwt.api.client.warehouse;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsStockStat extends JavaScriptObject {

	protected JsStockStat() {
	}

	public static final ProvidesKey<JsStockStat> PROVIDES_KEY = new ProvidesKey<JsStockStat>() {
		@Override
		public Object getKey(JsStockStat js) {
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
	
	public final native Double getInputs() /*-{
											return this.inputs;
											}-*/;
	
	public final native Double getOutputs() /*-{
											return this.outputs;
											}-*/;
	
	public final native Double getBalance() /*-{
											return this.balance;
											}-*/;
	
	public final native Double getDailyOutputs() /*-{
													return this.daily_outputs;
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
