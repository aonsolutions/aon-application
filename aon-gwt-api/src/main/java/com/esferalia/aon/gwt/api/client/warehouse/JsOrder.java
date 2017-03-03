package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsOrder extends JavaScriptObject {

	protected JsOrder() {}	public static final ProvidesKey<JsOrder> PROVIDES_KEY = new ProvidesKey<JsOrder>() {
		@Override
		public Object getKey(JsOrder carrierPacking) {
			return carrierPacking == null ? null : carrierPacking.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;

	public final native Integer getProject() /*-{
		return this.project;
	}-*/;
	
	public final native JsObject getRegistry() /*-{
		return this.registry;
	}-*/;

	public final native String getSeriesNumber() /*-{
		return this.series_number;
	}-*/;
	
	public final native String getSeries() /*-{
		return this.series;
	}-*/;

	public final native Integer getNumber() /*-{
		return this.number;
	}-*/;

	public final native String getPurchaseReference() /*-{
		return this.purchase_reference;
	}-*/;
	
	public final native Integer getAddress() /*-{
		return this.address;
	}-*/;
	
	public final native String getDiscountExpr() /*-{
		return this.discount_expr;
	}-*/;
	
	public final native String getIssueDate() /*-{
		return this.issue_date;
	}-*/;
	
	public final native Integer getPayMethod() /*-{
		return this.pay_method;
	}-*/;
	
	public final native JsObject getDocumentType() /*-{	
		return this.document_type;
	}-*/;
	
	public final native JsObject getSecurityLevel() /*-{	
		return this.security_level;
	}-*/;
	
	public final native JsObject getStatus() /*-{	
		return this.status;
	}-*/;
	
	public final native String getComments() /*-{
		return this.comments;
	}-*/;
	
	public final native String getRemarks() /*-{
		return this.remarks;
	}-*/;
	
	public final native Integer getWorkplace() /*-{
		return this.workplace;
	}-*/;
	
	public final native Integer getWarehouse() /*-{
		return this.warehouse;
	}-*/;

	public final native Integer getScope() /*-{
		return this.scope;
	}-*/;

	public final native Integer getNumberOfPymnts() /*-{
		return this.number_of_pymnts;
	}-*/;
	
	public final native Integer getDaysToFirstPymnt() /*-{
		return this.days_to_first_pymnt;
	}-*/;
	
	public final native Integer getDaysBetweenPymnts() /*-{
		return this.days_between_pymnts;
	}-*/;
	
	public final native String getPymntDays() /*-{
		return this.pymnt_days;
	}-*/;

	public final native String getBankAccount() /*-{
		return this.bank_account;
	}-*/;
	
	public final native String getBankAlias() /*-{
		return this.bank_alias;
	}-*/;
	
	public final native String getBic() /*-{
		return this.bic;
	}-*/;
	
	public final native Integer getCarrier() /*-{
		return this.carrier;
	}-*/;
	
	public final native String getNumberPlate() /*-{
		return this.number_plate;
	}-*/;

	public final native String getDriver() /*-{	
		return this.driver;
	}-*/;	

	public final native String getDriverDocument() /*-{
		return this.driver_document;
	}-*/;
	
	public final native Double getTotalPackages() /*-{
		return this.total_packages;
	}-*/;
	
	public final native Double getTotalWeight() /*-{
		return this.total_weight;
	}-*/;
	
	public final native String getShippingAlternativeAddress() /*-{
		return this.shipping_alternative_address;
	}-*/;
	
	public final native String getShippingAlternativeAddress2() /*-{
		return this.shipping_alternative_address2;
	}-*/;
	
	public final native String getShippingAlternativeZip() /*-{
		return this.shipping_alternative_zip;
	}-*/;
	
	public final native String getShippingAlternativeCity() /*-{
		return this.shipping_alternative_city;
	}-*/;
	
	public final native String getShippingAlternativePhone() /*-{
		return this.shipping_alternative_phone;
	}-*/;
	
	public final native String getShippingAlternativeRecipient() /*-{
		return this.shipping_alternative_recipient;
	}-*/;

	public final native String getShippingContact() /*-{
		return this.shipping_contact;
	}-*/;

	public final native JsObject getShippingPeriod() /*-{
		return this.shipping_period;
	}-*/;	
	
	public final native String getCreationUser() /*-{
		return this.creation_user;
	}-*/;
	
	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;

	public final native String getModificationUser() /*-{
		return this.modification_user;
	}-*/;

	public final native String getModificationDate() /*-{
		return this.modification_date;
	}-*/;
	
	public final native Integer getCarrierPacking() /*-{
		return this.carrier_packing;
	}-*/;
	
	public final native String getOrderType()/*-{
		return this.order_type;
	}-*/;

}
