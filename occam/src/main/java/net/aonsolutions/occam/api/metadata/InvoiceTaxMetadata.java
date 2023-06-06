package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum InvoiceTaxMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	 ,TAX_TYPE
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitTaxType(t);} }
	 ,PERCENT
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitPercent(t);} }
	 ,SURCHARGE_PERCENT
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitSurchargePercent(t);} }
	 ,DEDUCTIBLE_PERCENT
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitDeductiblePercent(t);} }
	 ,VAT_DEDUCTION_TYPE
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitVatDeductionType(t);} }
	 ,WITHHOLDING_TYPE
	 	{ @Override public <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> v, T t) { return v.visitWithholdingType(t);} }
	;
	public abstract <R,T> R visit(InvoiceTaxMetadataVisitor<R,T> visitor, T t);

	public static interface InvoiceTaxMetadataVisitor<R,T> {
		R visitId( T t );
		R visitTaxType( T t );
		R visitPercent( T t );
		R visitSurchargePercent( T t );
		R visitDeductiblePercent( T t );
		R visitVatDeductionType( T t );
		R visitWithholdingType( T t );
	}
}
