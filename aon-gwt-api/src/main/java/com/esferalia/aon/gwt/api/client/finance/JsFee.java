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

}
