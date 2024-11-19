package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum InvoiceFiscalMetadata implements Serializable {
	 INVOICE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitInvoice();} }
	,DOMAIN { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitDomain();} }
	,ISSUE_DATE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitIssueDate();} }
	,TAX_DATE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitTaxDate();} }
	,EXP_DATE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitExpDate();} }
	,VAT_GENERAL { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatGeneral();} }
	,VAT_SIMPLIFIED { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatSimplified();} }
	,VAT_SURCHARGE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatSurcharge();} }
	,VAT_ACCRUAL_PAYMENT { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatAccrualPayment();} }
	,VAT_REBU_OPERATION { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatRebuOperation();} }
	,VAT_REBU_PROFIT { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatRebuProfit();} }
	,VAT_TRAVEL_AGENCY { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatTravelAgency();} }
	,VAT_AGRICULTURE { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatAgriculture();} }
	,VAT_GOLD { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatGold();} }
	,VAT_UNION_EXTERNAL { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatUnionExternal();} }
	,VAT_UNION { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatUnion();} }
	,VAT_IMPORTATION { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatImportation();} }
	,VAT_EXEMPT { @Override public <T> T visit(InvoiceFiscalMetadataVisitor<T> v) { return v.visitVatExempt();} }
	;

	public abstract <T> T visit(InvoiceFiscalMetadataVisitor<T> v);
	public static interface InvoiceFiscalMetadataVisitor<T> {
		T visitInvoice();
		T visitDomain();
		T visitIssueDate();
		T visitTaxDate();
		T visitExpDate();
		T visitVatGeneral();
		T visitVatSimplified();
		T visitVatSurcharge();
		T visitVatAccrualPayment();
		T visitVatRebuOperation();
		T visitVatRebuProfit();
		T visitVatTravelAgency();
		T visitVatAgriculture();
		T visitVatGold();
		T visitVatUnionExternal();
		T visitVatUnion();
		T visitVatImportation();
		T visitVatExempt();
	}
}
