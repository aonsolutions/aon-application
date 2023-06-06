package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum InvoiceMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,ACTIVITY
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitActivity(t);} }
	,SERIES
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitSeries(t);} }
	,NUMBER
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitNumber(t);} }
	,REFERENCE_CODE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitReferenceCode(t);} }
	,ISSUE_DATE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitIssueDate(t);} }
	,TAX_DATE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitTaxDate(t);} }
	,CONFIDENTIAL
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitConfidential(t);} }
	,REGISTRY
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitRegistry(t);} }
	,DOCUMENT
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitDocument(t);} }
	,DOCUMENT_TYPE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitDocumentType(t);} }
	,DOCUMENT_COUNTRY
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitDocumentCountry(t);} }
	,NAME
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,SCOPE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitScope(t);} }
	,TYPE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitType(t);} }
	,TRANSACTION
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitTransaction(t);} }
	,SURCHARGE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitSurcharge(t);} }
	,WITHHOLDING
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitWithholding(t);} }
	,WITHHOLDING_FARMER
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitWithholdingFarmer(t);} }
	,VAT_ACCRUAL_PAYMENT
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitVatAccrualPayment(t);} }
	,INVESTMENT
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitInvestment(t);} }
	,SERVICE
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitService(t);} }
	,ANNULLED
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitAnnulled(t);} }
	,TOTAL
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitTotal(t);} }
	,AUDIT
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitAudit(t);} }
	,DETAILS
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitDetails(t);} }
	,BREAKDOWN
		{ @Override public <R,T> R visit(InvoiceMetadataVisitor<R,T> v, T t) { return v.visitBreakdown(t);} }
	;
	public abstract <R,T> R visit(InvoiceMetadataVisitor<R,T> visitor, T t);

	public static interface InvoiceMetadataVisitor<R,T> {
		R visitId( T t );
		R visitDomain( T t );
		R visitActivity( T t );
		R visitSeries( T t );
		R visitNumber( T t );
		R visitReferenceCode( T t );
		R visitIssueDate( T t );
		R visitTaxDate( T t );
		R visitConfidential( T t );
		R visitRegistry( T t );
		R visitDocument( T t );
		R visitDocumentType( T t );
		R visitDocumentCountry( T t );
		R visitName( T t );
		R visitScope( T t );
		R visitType( T t );
		R visitTransaction( T t );
		R visitSurcharge( T t );
		R visitWithholding( T t );
		R visitWithholdingFarmer( T t );
		R visitVatAccrualPayment( T t );
		R visitInvestment( T t );
		R visitService( T t );
		R visitAnnulled( T t );
		R visitTotal( T t );
		R visitAudit( T t );
		R visitDetails( T t );
		R visitBreakdown( T t );
	}
}
