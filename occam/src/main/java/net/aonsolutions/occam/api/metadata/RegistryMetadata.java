package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum RegistryMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitDomain();} }
	,DOCUMENT
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitDocument();} }
	,DOCUMENT_TYPE
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitDocumentType();} }
	,DOCUMENT_COUNTRY
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitDocumentCountry();} }
	,NAME
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitName();} }
	,ALIAS
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitAlias();} }
	,NATIONALITY
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitNationality();} }
	,CONFIDENTIAL
		{ @Override public <R> R visit(RegistryMetadataVisitor<R> v) { return v.visitConfidential();} }	
	;
	public abstract <R> R visit(RegistryMetadataVisitor<R> visitor);

	public static interface RegistryMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitDocument();
		 R visitDocumentType();
		 R visitDocumentCountry();
		 R visitName();
		 R visitAlias();
		 R visitNationality();
		 R visitConfidential();
		 
	}
}
