package com.esferalia.aon.gwt.api.client.finance;

import com.google.gwt.core.client.JavaScriptObject;

public class JsFee extends JavaScriptObject {

	protected JsFee() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getCustomer() /*-{
		return this.customer;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native String getStartDate() /*-{
		return this.start_date;
	}-*/;
	
	public final native String getEndDate() /*-{
		return this.end_date;
	}-*/;
	
	public final native String getBillingMonth() /*-{
		return this.billing_month;
	}-*/;
	
	public final native String getBillingYear() /*-{
		return this.billing_year;
	}-*/;
	
	public final native String getPeriod() /*-{
		return this.period;
	}-*/;

}
