
package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum InvoiceTaxMetadata implements Serializable {
	 ID { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitDomain();} }
	,INVOICE_DETAIL { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitInvoiceDetail();} }
	,TAX_TYPE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitTaxType();} }
	,BASE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitBase();} }
	,PERCENTAGE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitPercentage();} }
	,SURCHARGE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitSurcharge();} }
	,QUOTA { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitQuota();} }
	,SURCHARGE_QUOTA { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitSurchargeQuota();} }
	,VAT_DEDUCTION_TYPE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitVatDeductionType();} }
	,WITHHOLDING_TYPE { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitWithholdingType();} }
	,DEDUCTIBLE_PERCENT { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitDeductiblePercent();} }
	,DEDUCTIBLE_QUOTA { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitDeductibleQuota();} }
	,WITHHOLDING_ACCOUNT { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitWithholdingAccount();} }
	,OUTPUT_ACCOUNT { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitOutputAccount();} }
	,INPUT_ACCOUNT { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitInputAccount();} }
	,ADJ_ACCOUNT { @Override public <T> T visit(InvoiceTaxMetadataVisitor<T> v) { return v.visitAdjAccount();} }
	;

	public abstract <T> T visit(InvoiceTaxMetadataVisitor<T> v);
	public static interface InvoiceTaxMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitInvoiceDetail();
		T visitTaxType();
		T visitBase();
		T visitPercentage();
		T visitSurcharge();
		T visitQuota();
		T visitSurchargeQuota();
		T visitVatDeductionType();
		T visitWithholdingType();
		T visitDeductiblePercent();
		T visitDeductibleQuota();
		T visitWithholdingAccount();
		T visitOutputAccount();
		T visitInputAccount();
		T visitAdjAccount();
	}
	
}
