package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum AccountMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,CODE
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitCode(t);} }
	,DESCRIPTION
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitDescription(t);} }
	,ALIAS
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitAlias(t);} }
	,ACTIVE
		{ @Override public <R,T> R visit(AccountMetadataVisitor<R,T> v, T t) { return v.visitActive(t);} }
	;
	public abstract <R,T> R visit(AccountMetadataVisitor<R,T> visitor, T t);

	public static interface AccountMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitCode( T t );
		 R visitDescription( T t );
		 R visitAlias( T t );
		 R visitActive( T t );
	}
}
