package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum DomainMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitId();} }
	,NAME
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitName();} }
	,DESCRIPTION
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitDescription();} }
	,TYPE
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitType();} }
	,ACTIVE
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitActive();} }
	,SCOPE
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitScope();} }
	,PARENT
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitParent();} }
	,INHERITANCE
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitInheritance();} }
	, BOOKING
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitBooking();} }
	, AUDIT
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitAudit();} }
	, CONFIGURATION
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitConfiguration();} }
	, COMPANY
		{ @Override public <R> R visit(DomainMetadataVisitor<R> v) { return v.visitCompany();} }
	;
	public abstract <R> R visit(DomainMetadataVisitor<R> visitor);

	public static interface DomainMetadataVisitor<R> {
		R visitId();
		R visitName();
		R visitDescription();
		R visitType();
		R visitActive();
		R visitScope();
		R visitParent();
		R visitInheritance();
		R visitBooking();
		R visitAudit();
		R visitConfiguration();
		R visitCompany();
	}
}
