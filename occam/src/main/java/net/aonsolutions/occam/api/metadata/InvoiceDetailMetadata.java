package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum InvoiceDetailMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,ITEM
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitItem(t);} }
	,LINE
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitLine(t);} }
	,DESCRIPTION
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitDescription(t);} }
	,QUANTITY
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitQuantity(t);} }
	,PRICE
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitPrice(t);} }
	,DISCOUNT_EXPRESSION
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitDiscountExpression(t);} }
	,TAXABLE_BASE
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitTaxableBase(t);} }
	,PREPAYMENT
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitPrepayment(t);} }
	,SOURCE
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitSource(t);} }
	,SOURCE_ID
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitSourceId(t);} }
	,TAXES
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitTaxes(t);} }
	,AUDIT	
		{ @Override public <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> v, T t) { return v.visitAudit(t);} }
	;
	public abstract <R,T> R visit(InvoiceDetailMetadataVisitor<R,T> visitor, T t);

	public static interface InvoiceDetailMetadataVisitor<R,T> {
		R visitId( T t );
		R visitItem( T t );
		R visitLine( T t );
		R visitDescription( T t );
		R visitQuantity( T t );
		R visitPrice( T t );
		R visitDiscountExpression( T t );
		R visitTaxableBase( T t );
		R visitPrepayment( T t );
		R visitSource( T t );
		R visitSourceId( T t );
		R visitTaxes( T t );
		R visitAudit( T t );
	}
}
