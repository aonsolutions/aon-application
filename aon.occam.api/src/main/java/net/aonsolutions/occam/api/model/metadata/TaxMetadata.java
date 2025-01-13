package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum TaxMetadata implements Serializable {
	 ID 				{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN 			{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME 				{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitName();} }
	,TAX_TYPE 			{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitTaxType();} }
	,PERCENTAGE 		{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitPercentage();} }
	,SURCHARGE 			{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitSurcharge();} }
	,START_DATE 		{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitStartDate();} }
	,VAT_DEDUCTION_TYPE { @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitVatDeductionType();} }
	,WITHHOLDING_TYPE 	{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitWithholdingType();} }
	,SALES_ACCOUNT 		{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitSalesAccount();} }
	,PURCHASE_ACCOUNT 	{ @Override public <T> T visit(TaxMetadataVisitor<T> v) { return v.visitPurchaseAccount();} }
	;

	public abstract <T> T visit(TaxMetadataVisitor<T> v);
	public static interface TaxMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitTaxType();
		T visitPercentage();
		T visitSurcharge();
		T visitStartDate();
		T visitVatDeductionType();
		T visitWithholdingType();
		T visitSalesAccount();
		T visitPurchaseAccount();
	}
}
