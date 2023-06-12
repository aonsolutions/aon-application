package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum BookingMetadata implements Serializable {
	 ID
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitId();} }
	,EXPIRATION_DATE
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitExpirationDate();} }
	,OWNER
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitOwner();} }
	,DOMAIN_MANAGEMENT
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitDomainManagement();} }
	,DISABLE_DOMAIN_MANAGEMENT
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitDisableDomainManagement();} }
	,MAX_DEFINED_USERS
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitMaxDefinedUsers();} }
	,AON_CUSTOMER
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitAonCustomer();} }
	,AON_STATUS
		{ @Override public void visit(BookingMetadataVisitor v) { v.visitAonStatus();} }
	;
	public abstract void visit(BookingMetadataVisitor visitor);

	public static interface BookingMetadataVisitor {
		 void visitId();
		 void visitExpirationDate();
		 void visitOwner();
		 void visitDomainManagement();
		 void visitDisableDomainManagement();
		 void visitMaxDefinedUsers();
		 void visitAonCustomer();
		 void visitAonStatus();
	}
}
