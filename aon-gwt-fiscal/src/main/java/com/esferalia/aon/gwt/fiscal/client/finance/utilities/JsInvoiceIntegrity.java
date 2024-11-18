package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JavaScriptObject;

public class JsInvoiceIntegrity extends JavaScriptObject {

	protected JsInvoiceIntegrity() {
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
	public final native int getInvoiceType() /*-{
		return this.invoiceType;
	}-*/;
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getDocumentNumber() /*-{
		return this.documentNumber;
	}-*/;
	public final native String getReferenceCode() /*-{
		return this.referenceCode;
	}-*/;
	public final native String getRegistryDocument() /*-{
		return this.registryDocument;
	}-*/;
	public final native int getRegistryDocumentType() /*-{
		return this.registryDocumentType;
	}-*/;
	public final native String getRegistryDocumentCountry() /*-{
		return this.registryDocumentCountry;
	}-*/;
	public final native int getRegistry() /*-{
		return this.registry;
	}-*/;
	
	public final native String getRegistryName() /*-{
		return this.registryName;
	}-*/;
	public final native String getIssueDateString() /*-{
		return this.issueDate;
	}-*/;
	public final Date getIssueDate() {
		return AonStringUtils.mapIfNotBlank(getIssueDateString(), AonDateUtils::fromLong );
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final Date getTaxDate() {
		return AonStringUtils.mapIfNotBlank(getTaxDateString(), AonDateUtils::fromLong );
	}
	public final native double getTotal() /*-{
		return this.total;
	}-*/;
	public final native String getError() /*-{
		return this.error;
	}-*/;
	
	public final native int getAccountEntryId() /*-{
		return this.accountEntryId;
	}-*/;
}
