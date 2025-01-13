package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.metadata.MetadataVisitor.InvoiceAddressMetadataVisitor;

public enum InvoiceAddressMetadata implements Serializable {
	ID { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitDomain();} }
	,INVOICE { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitInvoice();} }
	,STREET_TYPE { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitStreetType();} }
	,ADDRESS { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitAddress();} }
	,NUMBER { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitNumber();} }
	,ADDRESS2 { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitAddress2();} }
	,ZIP { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitZip();} }
	,CITY { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitCity();} }
	,PROVINCE { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitProvince();} }
	,GEOZONE { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitGeozone();} }
	,PARENT { @Override public <T> T visit(InvoiceAddressMetadataVisitor<T> v) { return v.visitParent();} }
	,	;

	public abstract <T> T visit(InvoiceAddressMetadataVisitor<T> v);
}
