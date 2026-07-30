package com.esferalia.aon.gwt.fiscal.client.report;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.google.gwt.core.client.JavaScriptObject;

public class JsOperationBreakdownNew extends JavaScriptObject {
	
	protected JsOperationBreakdownNew() {
	}
	
	public final native int getEntryId() /*-{
		return this.entryId;
	}-*/;
	
	public final native int getEntryJournal() /*-{
		return this.entryJournal;
	}-*/;
	
	private Date ensureDate(String dateString) {
		return dateString == null ? null : AonDateUtils.parseDate(dateString); // FECHAS FORMATEADAS COMO TEXTO dd/MM/yyyy
	}
	
	public final Date getEntryDate() {
		return ensureDate(getEntryDateString());
	}

	private final native String getEntryDateString() /*-{
		return this.entryDate;
	}-*/;
	
	public final Date getTaxDate() {
		return ensureDate(getTaxDateString());
	}
	
	private final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	
	public final native String getActivityIAE() /*-{
		return this.activityIAE;
	}-*/;
	public final native String getConceptCode() /*-{
		return this.conceptCode;
	}-*/;
	public final native double getConceptAmount() /*-{
		return this.conceptAmount;
	}-*/;
	public final native String getInvoiceSeries() /*-{
		return this.invoiceSeries;
	}-*/;
	public final native String getInvoiceNumber() /*-{
		return this.invoiceNumber;
	}-*/;
	public final native String getReceptionNumber() /*-{
		return this.receptionNumber;
	}-*/;
	public final native String getDocument() /*-{
		return this.document;
	}-*/;
	public final native String getName() /*-{
		return this.name;
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
	public final native double getDeductibleQuota() /*-{
 		return this.deductibleQuota;
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
	public final native double getRetentionPercent() /*-{
		return this.retentionPercent;
	}-*/;
	public final native double getRetentionQuota() /*-{
		return this.retentionQuota;
	}-*/;
	
	public final native boolean isInvoiceRECC() /*-{
		return this.operationKey == "07";
	}-*/;
	
	public final native double getPayAmount() /*-{
		return this.payAmount;
	}-*/;

	public final native String getConceptDescription() /*-{
		return this.conceptDescription;
	}-*/;
	
	public final native String getAccountCode() /*-{
		return this.accountCode;
	}-*/;
	
}
