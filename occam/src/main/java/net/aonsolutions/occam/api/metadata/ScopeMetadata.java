package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ScopeMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(ScopeMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(ScopeMetadataVisitor<R> v) { return v.visitDomain();} }
	,DESCRIPTION
		{ @Override public <R> R visit(ScopeMetadataVisitor<R> v) { return v.visitDescription();} }
	;
	public abstract <R> R visit(ScopeMetadataVisitor<R> visitor);

	public static interface ScopeMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitDescription();
	}
}
