package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum DomainMetadata implements Serializable {
	 ID
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitId();} }
	,NAME
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitName();} }
	,DESCRIPTION
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitDescription();} }
	,TYPE
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitType();} }
	,ACTIVE
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitActive();} }
	,SCOPE
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitScope();} }
	,PARENT
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitParent();} }
	,INHERITANCE
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitInheritance();} }
	, BOOKING
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitBooking();} }
	, AUDIT
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitAudit();} }
	, CONFIGURATION
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitConfiguration();} }
	, COMPANY
		{ @Override public void visit(DomainMetadataVisitor v) { v.visitCompany();} }
	;
	public abstract void visit(DomainMetadataVisitor visitor);

	public static interface DomainMetadataVisitor {
		void visitId();
		void visitName();
		void visitDescription();
		void visitType();
		void visitActive();
		void visitScope();
		void visitParent();
		void visitInheritance();
		void visitBooking();
		void visitAudit();
		void visitConfiguration();
		void visitCompany();
	}
}
