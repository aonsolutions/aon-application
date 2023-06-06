package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum RegistryMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,DOCUMENT
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitDocument(t);} }
	,DOCUMENT_TYPE
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitDocumentType(t);} }
	,DOCUMENT_COUNTRY
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitDocumentCountry(t);} }
	,NAME
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,ALIAS
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitAlias(t);} }
	,NATIONALITY
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitNationality(t);} }
	,CONFIDENTIAL
		{ @Override public <R,T> R visit(RegistryMetadataVisitor<R,T> v, T t) { return v.visitConfidential(t);} }	
	;
	public abstract <R,T> R visit(RegistryMetadataVisitor<R,T> visitor, T t);

	public static interface RegistryMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitDocument( T t );
		 R visitDocumentType( T t );
		 R visitDocumentCountry( T t );
		 R visitName( T t );
		 R visitAlias( T t );
		 R visitNationality( T t );
		 R visitConfidential( T t );
		 
	}
}
