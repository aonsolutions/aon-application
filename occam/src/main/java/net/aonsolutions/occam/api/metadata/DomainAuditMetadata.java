package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum DomainAuditMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitId();} }
	,LAST_ACCESS_USER
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitLastAccessUser();} }
	,LAST_ACCESS_DATE
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitLastAccessDate();} }
	,CREATION_USER
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitCreationUser();} }
	,CREATION_DATE
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitCreationDate();} }
	,MODIFICATION_USER
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitModificationUser();} }
	,MODIFICATION_DATE
		{ @Override public <R> R visit(DomainAuditMetadataVisitor<R> v) { return v.visitModificationDate();} }
	;
	public abstract <R> R visit(DomainAuditMetadataVisitor<R> visitor);

	public static interface DomainAuditMetadataVisitor<R> {
		R visitId();
		R visitLastAccessUser();
		R visitLastAccessDate();
		R visitCreationUser();
		R visitCreationDate();
		R visitModificationUser();
		R visitModificationDate();
		 
	}
}
