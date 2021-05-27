package com.esferalia.aon.gwt.api.client.finance;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsFinance extends JavaScriptObject {

	public static final ProvidesKey<JsFinance> PROVIDES_KEY = new ProvidesKey<JsFinance>() {
		@Override
		public Object getKey(JsFinance invoice) {
			return invoice == null ? null : invoice.getId();
		}
	};
	
	protected JsFinance() {
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
	
	public final native String getIssueDate() /*-{
		return this.issue_date;
	}-*/;
	
	public final native String getTaxDate() /*-{
		return this.tax_date;
	}-*/;
	
	public final native Boolean isVatAccrualPayment() /*-{
		return this.vat_accrual_payment;
	}-*/;

	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;
	
	public final native String getCreationUser() /*-{
		return this.creation_user;
	}-*/;

	public final native String getModificationDate() /*-{
		return this.modification_date;
	}-*/;
	
	public final native String getModificationUser() /*-{
		return this.modification_user;
	}-*/;
	
	
	// SII

	public final native String getSiiStatus() /*-{	
		return this.sii_status;
	}-*/;
	
	public final native String getSii() /*-{
		return this.sii;
	}-*/;

	
	public final native Boolean isSiiSent() /*-{
		return this.sii_sent;
	}-*/;
	
	public final native Boolean isSiiSent2() /*-{
		return this.sii_sent2;
	}-*/;
}
