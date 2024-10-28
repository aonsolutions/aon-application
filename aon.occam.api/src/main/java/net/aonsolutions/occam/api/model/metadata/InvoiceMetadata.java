package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum InvoiceMetadata implements Serializable {
	 HEADER { @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitHeader();} }
	,RECTIFICATION_INVOICE { @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitRectificationInvoice();} }
	,ADDRESS { @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitAddress();} }
	,DETAILS { @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitDetails();} }
	,FINANCES{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitFinances();} }
	,TAX_BREAKDOWN{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitTaxBreakdown();} }
	,FISCAL{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitFiscal();} }
	,ATTACH{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitAttach();} }
	,MESSAGES{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitMessages();} }
	,RAWDOC_ID{ @Override public <T> T visit(InvoiceMetadataVisitor<T> v) { return v.visitRawdocId();} }
	;

	public abstract <T> T visit(InvoiceMetadataVisitor<T> v);
	public static interface InvoiceMetadataVisitor<T> {
		T visitHeader();
		T visitRectificationInvoice();
		T visitAddress();
		T visitDetails();
		T visitFinances();
		T visitTaxBreakdown();
		T visitFiscal();
		T visitAttach();
		T visitRawdocId();
		T visitMessages();
	}
}
