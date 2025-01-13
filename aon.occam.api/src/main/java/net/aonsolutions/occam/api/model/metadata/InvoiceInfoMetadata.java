package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.metadata.MetadataVisitor.InvoiceInfoMetadataVisitor;

public enum InvoiceInfoMetadata implements Serializable {
	
	 ID { @Override public <T> T visit(InvoiceInfoMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(InvoiceInfoMetadataVisitor<T> v) { return v.visitDomain();} }
	,INVOICE { @Override public <T> T visit(InvoiceInfoMetadataVisitor<T> v) { return v.visitInvoice();} }
	,TYPE { @Override public <T> T visit(InvoiceInfoMetadataVisitor<T> v) { return v.visitType();} }
	,STATUS { @Override public <T> T visit(InvoiceInfoMetadataVisitor<T> v) { return v.visitStatus();} }
	;

	public abstract <T> T visit(InvoiceInfoMetadataVisitor<T> v);
}
