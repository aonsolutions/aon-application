package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.core.client.JavaScriptObject;

public class JsIRPFBreakdown extends JavaScriptObject {
	
	protected JsIRPFBreakdown() {
	}
	
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	public final native String getEpigraph() /*-{
		return this.epigraph;
	}-*/;
	public final native String getRegistryDocument() /*-{
		return this.registryDocument;
	}-*/;
	public final native int getRegistryDocumentType() /*-{
		return this.registryDocumentType;
	}-*/;
	public final native int getRegistryDocumentCountry() /*-{
		return this.registryDocumentCountry;
	}-*/;
	public final native String getRegistryName() /*-{
		return this.registryName;
	}-*/;
	public final native String getIssueDateString() /*-{
		return this.issueDate;
	}-*/;
	public final Date getIssueDate() {
		return getIssueDateString() == null ? null : new Date( Long.valueOf(getIssueDateString()));
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final Date getTaxDate() {
		return getTaxDateString() == null ? null : new Date( Long.valueOf(getTaxDateString()));
	}
	public final native boolean isFromSalary() /*-{
		return this.fromSalary;
	}-*/;
	public final native boolean isInsidePeriod() /*-{
		return this.insidePeriod;
	}-*/;
	public final native int getInvoiceType() /*-{
		return this.invoiceType;
	}-*/;
	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
	public final native String getSeries() /*-{
		return this.series;
	}-*/;
	public final native int getNumber() /*-{
		return this.number;
	}-*/;
	public final native String getReferenceCode() /*-{
		return this.referenceCode;
	}-*/;
	public final native int getWithholdingType() /*-{
		return this.withholding_type;
	}-*/;
	public final native int getRegime() /*-{
		return this.regime;
	}-*/;
	public final native boolean isInKind() /*-{
		return this.inKind;
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
	public final native double getDeductiblePercent() /*-{
		return this.deductiblePercent;
	}-*/;
	public final native double getDeductibleQuota() /*-{
		return this.deductibleQuota;
	}-*/;
	public final native int getGroupByNif() /*-{
		return this.groupByNif;
	}-*/;
	public final native String getZip() /*-{
		return this.zip;
	}-*/;
	public final native String getCity() /*-{
		return this.city;
	}-*/;
	public final native boolean isValid( int number) /*-{
		return !isNaN(number);
	}-*/;
	
	public final String getDocumentNumber() {
		InvoiceType it = InvoiceType.safeValueOf(getInvoiceType());
		return ( it != null && isValid( getNumber() )) 
				?FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(getInvoiceType()), getSeries(), getNumber())
				:null;
	}
	public final boolean isSales() {
		return (getInvoiceType() == InvoiceType.SALES.ordinal());
	}
	
}
