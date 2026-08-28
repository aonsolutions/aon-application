package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JavaScriptObject;

public class JsVatContext extends JavaScriptObject {
	
	protected JsVatContext() {
	}
	
	public final native int getCounter() /*-{
		return this.counter;
	}-*/;
	
	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
	
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	public final native int getVatRegime() /*-{
		return this.vatRegime;
	}-*/;
	public final native boolean isVatSurchargeRegime() /*-{
		return this.vatSurchargeRegime;
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
	public final native boolean isInsidePeriod() /*-{
		return this.insidePeriod;
	}-*/;
	public final native int getInvoiceType() /*-{
		return this.invoiceType;
	}-*/;
	public final native int getRectificationType() /*-{
		return this.rectificationType;
	}-*/;
	public final native int getRectificationInvoice() /*-{
		return this.rectificationInvoice;
	}-*/;

	public final native boolean isService() /*-{
		return this.service;
	}-*/;
	public final native int getTransaction() /*-{
		return this.transaction;
	}-*/;
	public final native boolean isInvestment() /*-{
		return this.investment;
	}-*/;
	public final native boolean isVatAccrualRegime() /*-{
		return this.vatAccrualRegime;
	}-*/;
	public final native int getVatDeductionType() /*-{
		return this.vatDeductionType;
	}-*/;
	public final native boolean isFarmerRegime() /*-{
		return this.farmerRegime;
	}-*/;
	public final native double getBase() /*-{
		return this.base;
	}-*/;
	public final native double getPercentage() /*-{
		return this.percentage;
	}-*/;
	public final native double getQuota() /*-{
		return this.quota;
	}-*/;
	public final native int getInvestAsset() /*-{
		return this.investAsset;
	}-*/;
	public final native double getDeductiblePercent() /*-{
		return this.deductiblePercent;
	}-*/;
	public final native double getDeductibleQuota() /*-{
		return this.deductibleQuota;
	}-*/;
	public final native boolean isSurcharge() /*-{
		return this.surcharge;
	}-*/;
	public final native double getSurchargePercent() /*-{
		return this.surchargePercent;
	}-*/;
	public final native double getSurchargeQuota() /*-{
		return this.surchargeQuota;
	}-*/;
	public final native boolean isProrrated() /*-{
		return this.prorrated;
	}-*/;
	public final native double getProrratePercent() /*-{
		return this.prorratePercent;
	}-*/;
	public final native double getProrrateQuota() /*-{
		return this.prorrateQuota;
	}-*/;
	public final native boolean isVatUnion() /*-{
		return this.vatUnion;
	}-*/;
	public final native boolean isVatUnionExternal() /*-{
		return this.vatUnionExternal;
	}-*/;
	public final native boolean isVatImportation() /*-{
		return this.vatImportation;
	}-*/;
	public final native boolean hasDuaLinked() /*-{
		return this.duaLinked;
	}-*/;
	public final native double getAmount347() /*-{
		return this.amount347;
	}-*/;
	
	public final boolean isRectification() {
		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER.ordinal());
	}
	public final boolean isSales() {
		return (getInvoiceType() == InvoiceType.SALES.ordinal());
	}

}
