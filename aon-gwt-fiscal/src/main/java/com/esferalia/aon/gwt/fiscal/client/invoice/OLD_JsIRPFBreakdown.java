package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class OLD_JsIRPFBreakdown extends JavaScriptObject {
	
	private static DateTimeFormat DATE_FORMAT = null;

	protected OLD_JsIRPFBreakdown() {
	}
	
	private DateTimeFormat getDateTimeFormat() {
		if (DATE_FORMAT == null)
			DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
		return DATE_FORMAT;
	}
	
//	public final native int getCounter() /*-{
//		return this.counter;
//	}-*/;
//	
	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
//	
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	public final native int getRegime() /*-{
		return this.regime;
	}-*/;
	public final native String getEpigraph() /*-{
		return this.epigraph;
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
	public final native int getRegistryDocumentCountry() /*-{
		return this.registryDocumentCountry;
	}-*/;
//	public final native int getRegistr() /*-{
//		return this.registry;
//	}-*/;
//	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	public final native String getIssueDateString() /*-{
		return this.issueDate;
	}-*/;
	public final Date getIssueDate() {
		return getDateTimeFormat().parse(getIssueDateString());
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final Date getTaxDate() {
		return getDateTimeFormat().parse(getTaxDateString());
	}
//	public final native boolean isInsidePeriod() /*-{
//		return this.insidePeriod;
//	}-*/;
	public final native int getInvoiceType() /*-{
		return this.invoiceType;
	}-*/;
	public final native int getWithholdingType() /*-{
		return this.withholdingType;
	}-*/;
//	public final native int getRectificationInvoice() /*-{
//		return this.rectificationInvoice;
//	}-*/;
//
//	public final native boolean isService() /*-{
//		return this.service;
//	}-*/;
//	public final native int getTransaction() /*-{
//		return this.transaction;
//	}-*/;
//	public final native boolean isInvestment() /*-{
//		return this.investment;
//	}-*/;
//	public final native boolean isVatAccrualRegime() /*-{
//		return this.vatAccrualRegime;
//	}-*/;
//	public final native int getVatDeductionType() /*-{
//		return this.vatDeductionType;
//	}-*/;
//	public final native boolean isFarmerRegime() /*-{
//		return this.farmerRegime;
//	}-*/;
	public final native double getBase() /*-{
		return this.base;
	}-*/;
	public final native double getPercent() /*-{
		return this.percent;
	}-*/;
	public final native double getQuota() /*-{
		return this.quota;
	}-*/;
//	public final native int getInvestAsset() /*-{
//		return this.investAsset;
//	}-*/;
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
//	public final native boolean isSurcharge() /*-{
//		return this.surcharge;
//	}-*/;
//	public final native double getSurchargePercent() /*-{
//		return this.surchargePercent;
//	}-*/;
//	public final native double getSurchargeQuota() /*-{
//		return this.surchargeQuota;
//	}-*/;
//	
//	public final boolean isRectification() {
//		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER.ordinal());
//	}
	public final boolean isSales() {
		return (getInvoiceType() == InvoiceType.SALES.ordinal());
	}
}
