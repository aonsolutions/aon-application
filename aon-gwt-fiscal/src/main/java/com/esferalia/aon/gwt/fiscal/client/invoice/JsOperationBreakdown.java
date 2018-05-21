package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsOperationBreakdown extends JavaScriptObject {
	
	private static DateTimeFormat DATE_FORMAT = null;

	protected JsOperationBreakdown() {
	}
	
	private DateTimeFormat getDateTimeFormat() {
		if (DATE_FORMAT == null)
			DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
		return DATE_FORMAT;
	}
	
	public final native int getCounter() /*-{
		return this.counter;
	}-*/;
	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
	public final native String getAccount() /*-{
		return this.account;
	}-*/;
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	public final native String getDocumentNumber() /*-{
		return this.documentNumber;
	}-*/;
	public final native String getRegistryDocument() /*-{
		return this.registryDocument;
	}-*/;
	public final native String getRegistryName() /*-{
		return this.registryName;
	}-*/;
	public final native String getName() /*-{
		return this.name;
	}-*/;
	public final native String getConcept() /*-{
		return this.concept;
	}-*/;
	public final native String getAccountDescription() /*-{
		return this.accountDescription;
	}-*/;	
	public final native String getEntryDateString() /*-{
		return this.entryDate;
	}-*/;
	public final Date getEntryDate() {
		return getDateTimeFormat().parse(getEntryDateString());
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final Date getTaxDate() {
		return getDateTimeFormat().parse(getTaxDateString());
	}
	public final native double getBase() /*-{
		return this.base;
	}-*/;
	public final native double getQuota() /*-{
		return this.quota;
	}-*/;
	public final native double getPercent() /*-{
		return this.percent;
	}-*/;
	public final native double getTotal() /*-{
		return this.total;
	}-*/;
	public final native double getSurchargePercent() /*-{
		return this.surchargePercent;
	}-*/;
	public final native double getSurchargeQuota() /*-{
		return this.surchargeQuota;
	}-*/;
	public final native boolean isInsidePeriod() /*-{
		return this.insidePeriod;
	}-*/;

	public final native boolean getExpenses() /*-{
		return this.expenses;
	}-*/;
	public final native boolean getIrpf() /*-{
		return this.irpf;
	}-*/;
}
