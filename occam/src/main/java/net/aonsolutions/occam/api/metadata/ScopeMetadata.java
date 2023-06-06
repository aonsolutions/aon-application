package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ScopeMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(ScopeMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(ScopeMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,DESCRIPTION
		{ @Override public <R,T> R visit(ScopeMetadataVisitor<R,T> v, T t) { return v.visitDescription(t);} }
	;
	public abstract <R,T> R visit(ScopeMetadataVisitor<R,T> visitor, T t);

	public static interface ScopeMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitDescription( T t );
	}
}
