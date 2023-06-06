package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum DomainMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,NAME
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,DESCRIPTION
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitDescription(t);} }
	,TYPE
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitType(t);} }
	,ACTIVE
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitActive(t);} }
	,SCOPE
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitScope(t);} }
	,PARENT
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitParent(t);} }
	,INHERITANCE
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitInheritance(t);} }
	, BOOKING
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitBooking(t);} }
	, AUDIT
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitAudit(t);} }
	, CONFIGURATION
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitConfiguration(t);} }
	, COMPANY
		{ @Override public <R,T> R visit(DomainMetadataVisitor<R,T> v, T t) { return v.visitCompany(t);} }
	;
	public abstract <R,T> R visit(DomainMetadataVisitor<R,T> visitor, T t);

	public static interface DomainMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitName( T t );
		 R visitDescription( T t );
		 R visitType( T t );
		 R visitActive( T t );
		 R visitScope( T t );
		 R visitParent( T t );
		 R visitInheritance( T t );
		 R visitBooking( T t );
		 R visitAudit( T t );
		 R visitConfiguration( T t );
		 R visitCompany( T t );
	}
}
