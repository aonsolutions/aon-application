package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum BookingMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,EXPIRATION_DATE
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitExpirationDate(t);} }
	,OWNER
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitOwner(t);} }
	,DOMAIN_MANAGEMENT
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitDomainManagement(t);} }
	,DISABLE_DOMAIN_MANAGEMENT
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitDisableDomainManagement(t);} }
	,MAX_DEFINED_USERS
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitMaxDefinedUsers(t);} }
	,AON_CUSTOMER
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitAonCustomer(t);} }
	,AON_STATUS
		{ @Override public <R,T> R visit(BookingMetadataVisitor<R,T> v, T t) { return v.visitAonStatus(t);} }
	;
	public abstract <R,T> R visit(BookingMetadataVisitor<R,T> visitor, T t);

	public static interface BookingMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitExpirationDate( T t );
		 R visitOwner( T t );
		 R visitDomainManagement( T t );
		 R visitDisableDomainManagement( T t );
		 R visitMaxDefinedUsers( T t );
		 R visitAonCustomer( T t );
		 R visitAonStatus( T t );
	}
}
