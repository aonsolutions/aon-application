package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum InvoiceBreakdownMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	 ,TAX_TYPE
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitTaxType(t);} }
	 ,BASE
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitBase(t);} }
	 ,PERCENT
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitPercent(t);} }
	 ,QUOTA
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitQuota(t);} }
	 ,SURCHARGE_PERCENT
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitSurchargePercent(t);} }
	 ,SURCHARGE_QUOTA
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitSurchargeQuota(t);} }
	 ,DEDUCTIBLE_PERCENT
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitDeductiblePercent(t);} }
	 ,DEDUCTIBLE_QUOTA
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitDeductibleQuota(t);} }
	 ,VAT_DEDUCTION_TYPE
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitVatDeductionType(t);} }
	 ,WITHHOLDING_TYPE
	 	{ @Override public <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> v, T t) { return v.visitWithholdingType(t);} }
	;
	public abstract <R,T> R visit(InvoiceBreakdownMetadataVisitor<R,T> visitor, T t);

	public static interface InvoiceBreakdownMetadataVisitor<R,T> {
		R visitId( T t );
		R visitTaxType( T t );
		R visitBase( T t );
		R visitPercent( T t );
		R visitQuota( T t );
		R visitSurchargePercent( T t );
		R visitSurchargeQuota( T t );
		R visitDeductiblePercent( T t );
		R visitDeductibleQuota( T t );
		R visitVatDeductionType( T t );
		R visitWithholdingType( T t );
	}
}
