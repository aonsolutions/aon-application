package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum BookingMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitId();} }
	,EXPIRATION_DATE
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitExpirationDate();} }
	,OWNER
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitOwner();} }
	,DOMAIN_MANAGEMENT
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitDomainManagement();} }
	,DISABLE_DOMAIN_MANAGEMENT
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitDisableDomainManagement();} }
	,MAX_DEFINED_USERS
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitMaxDefinedUsers();} }
	,AON_CUSTOMER
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitAonCustomer();} }
	,AON_STATUS
		{ @Override public <R> R visit(BookingMetadataVisitor<R> v) { return v.visitAonStatus();} }
	;
	public abstract <R> R visit(BookingMetadataVisitor<R> visitor);

	public static interface BookingMetadataVisitor<R> {
		 R visitId();
		 R visitExpirationDate();
		 R visitOwner();
		 R visitDomainManagement();
		 R visitDisableDomainManagement();
		 R visitMaxDefinedUsers();
		 R visitAonCustomer();
		 R visitAonStatus();
	}
}
