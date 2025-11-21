package com.esferalia.aon.gwt.fiscal.client.report.old;

import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsOperationBreakdown extends JavaScriptObject {
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");

	protected JsOperationBreakdown() {
	}
	
	public final native int getEntryId() /*-{
		return this.entryId;
	}-*/;
	public final Date getEntryDate() {
		return DATE_FORMAT.parse(getEntryDateString());
	}
	public final native String getEntryDateString() /*-{
		return this.entryDate;
	}-*/;
	public final Date getTaxDate() {
		return DATE_FORMAT.parse(getTaxDateString());
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final native String getAccount() /*-{
		return this.account;
	}-*/;
	public final native String getAccountDescription() /*-{
		return this.accountDescription;
	}-*/;
	public final native String getConcept() /*-{
		return this.concept;
	}-*/;
	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
	public final native String getRegistryDocument() /*-{
		return this.registryDocument;
	}-*/;
	public final native String getRegistryName() /*-{
		return this.registryName;
	}-*/;
	public final String getRegistryFullName() {
		return  AonStringUtils.abbreviate(
			AonStringUtils.defaultIfBlank(getRegistryDocument(), AonStringUtils.EMPTY)
			+ (AonStringUtils.isBlank(getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
			+ AonStringUtils.defaultIfBlank(getRegistryName(), AonStringUtils.EMPTY),34 );
	}
	public final native String getDocumentNumber() /*-{
		return this.documentNumber;
	}-*/;
	public final native double getBase() /*-{
		return this.base;
	}-*/;
	public final native double getPercent() /*-{
		return this.percent;
	}-*/;
	public final native double getQuota() /*-{
	 	return this.quota;
	}-*/;	
	public final native double getSurchargePercent() /*-{
		return this.surchargePercent;
	}-*/;
	public final native double getSurchargeQuota() /*-{
		return this.surchargeQuota;
	}-*/;
	public final native double getTotal() /*-{
		return this.total;
	}-*/;
	public final native String getEpigraph() /*-{
		return this.epigraph;
	}-*/;
	
}
