package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.google.gwt.core.client.JavaScriptObject;

public class JsVatContextNEW extends JavaScriptObject {
	
	protected JsVatContextNEW () {
	}

	public final native int getInvoice() /*-{
		return this.invoice;
	}-*/;
	
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	
	private VATRegime vatRegime;  // *****************************************************************
	
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
	
	private DocumentType registryDocumentType;	// *****************************************************************
	private Country registryDocumentCountry;	// *****************************************************************
	
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
		return new Date( Long.valueOf(getIssueDateString()));
	}
	public final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	public final Date getTaxDate() {
		return new Date( Long.valueOf(getTaxDateString()));
	}
	
	public final native String getCreationDateString() /*-{
		return this.creationDate;
	}-*/;
	public final Date getCreationDate() {
		return new Date( Long.valueOf(getCreationDateString()));
	}
	
	public final native String getRegContableDateString() /*-{
		return this.regContableDate;
	}-*/;
	public final Date getRegContableDate() {
		return new Date( Long.valueOf(getRegContableDateString()));
	}
	
	public final native String getDetailDescription() /*-{
		return this.detailDescription;
	}-*/;

	public final native boolean isInsidePeriod() /*-{
		return this.insidePeriod;
	}-*/;
	
	private InvoiceType invoiceType;		// *****************************************************************
	private RectificationType rectificationType;	// *****************************************************************
	
	public final native int getRectificationInvoice() /*-{
		return this.rectificationInvoice;
	}-*/;

	public final native boolean isService() /*-{
		return this.service;
	}-*/;

	private InvoiceTransactionType transaction;	// *****************************************************************
	
	public final native boolean isInvestment() /*-{
		return this.investment;
	}-*/;

	
	public final native boolean isVatAccrualRegime() /*-{
		return this.vatAccrualRegime;
	}-*/;

	private VatDeductionType vatDeductionType;	// *****************************************************************
	
	public final native boolean isFarmerRegime() /*-{
		return this.farmerRegime;
	}-*/;
	
	public final native boolean isPrepayment() /*-{
		return this.prepayment;
	}-*/;

	public final native boolean isVatImportation() /*-{
		return this.vatImportation;
	}-*/;

	public final native boolean isDuaLinked() /*-{
		return this.duaLinked;
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
	
	public final native String getSiiStatus() /*-{
		return this.siiStatus;
	}-*/;
	
	public final native String getAmortizationDescription() /*-{
		return this.amortizationDescription;
	}-*/;

	public final native double getAmortizationPercentage() /*-{
		return this.amortizationPercentage;
	}-*/;

	public final native String getAmortizationInitialDateString() /*-{
		return this.amortizationInitialDate;
	}-*/;
	public final Date getAmortizationInitialDate() {
		return new Date( Long.valueOf(getAmortizationInitialDateString()));
	}

	public final native boolean isFinancePending() /*-{
		return this.financePending;
	}-*/;

	public final native double getAmount347() /*-{
		return this.amount347;
	}-*/;

	public final native boolean hasRetention() /*-{
		return this.hasRetention;
	}-*/;

	public final native String getRectificateInvoiceTaxDateString() /*-{
		return this.rectificateInvoiceTaxDate;
	}-*/;
	public final Date getRectificateInvoiceTaxDate() {
		return new Date( Long.valueOf(getRectificateInvoiceTaxDateString()));
	}
	
	public final native int getRectificateYear() /*-{
		return this.rectificateYear;
	}-*/;
	
	private Period rectificatePeriod;	// *****************************************************************
	
}
