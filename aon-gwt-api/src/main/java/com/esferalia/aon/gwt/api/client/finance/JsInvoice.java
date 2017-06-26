package com.esferalia.aon.gwt.api.client.finance;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsInvoice extends JavaScriptObject {

	public static final ProvidesKey<JsInvoice> PROVIDES_KEY = new ProvidesKey<JsInvoice>() {
		@Override
		public Object getKey(JsInvoice invoice) {
			return invoice == null ? null : invoice.getId();
		}
	};
	
	protected JsInvoice() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getRegistry() /*-{
		return this.registry;
	}-*/;
	
	public final native String getRegistryName() /*-{
		return this.registry_name;
	}-*/;
	
	public final native String getReferenceCode() /*-{
		return this.reference_code;
	}-*/;
	
	public final native String getTaxDate() /*-{
		return this.tax_date;
	}-*/;

	
	public final native Boolean isSiiSent() /*-{
		return this.sii_sent;
	}-*/;
	
	public final native Boolean isVatAccrualPayment() /*-{
		return this.vat_accrual_payment;
	}-*/;

	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	
}
