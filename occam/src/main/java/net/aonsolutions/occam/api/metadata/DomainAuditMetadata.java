package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum DomainAuditMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,LAST_ACCESS_USER
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitLastAccessDate(t);} }
	,LAST_ACCESS_DATE
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitLastAccessDate(t);} }
	,CREATION_USER
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitCreationUser(t);} }
	,CREATION_DATE
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitCreationDate(t);} }
	,MODIFICATION_USER
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitModificationUser(t);} }
	,MODIFICATION_DATE
		{ @Override public <R,T> R visit(DomainAuditMetadataVisitor<R,T> v, T t) { return v.visitModificationDate(t);} }
	;
	public abstract <R,T> R visit(DomainAuditMetadataVisitor<R,T> visitor, T t);

	public static interface DomainAuditMetadataVisitor<R,T> {
		R visitId( T t );
		R visitLastAccessUser( T t );
		R visitLastAccessDate( T t );
		R visitCreationUser( T t );
		R visitCreationDate( T t );
		R visitModificationUser( T t );
		R visitModificationDate( T t );
		 
	}
}
