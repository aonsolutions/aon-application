package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsSales extends JavaScriptObject {

	protected JsSales() {
	}

	public static final ProvidesKey<JsSales> PROVIDES_KEY = new ProvidesKey<JsSales>() {
		@Override
		public Object getKey(JsSales sales) {
			return sales == null ? null : sales.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native String getSeries() /*-{
												return this.series;
												}-*/;

	public final native Integer getNumber() /*-{
											return this.number;
											}-*/;

	public final native JsObject getCustomer() /*-{
												return this.customer;
												}-*/;

	public final native String getIssueDate() /*-{
												return this.issue_date;
												}-*/;

	public final native String getDeliveryDate() /*-{
													return this.delivery_date;
													}-*/;

	public final native String getPurchaseReference() /*-{
														return this.purchase_reference;
														}-*/;

}
