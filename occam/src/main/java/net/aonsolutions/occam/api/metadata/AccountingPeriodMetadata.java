package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum AccountingPeriodMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,NAME
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,START_DATE
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitStartDate(t);} }
	,END_DATE
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitEndDate(t);} }
	,STATUS
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitStatus(t);} }
	,DEFAULT_PERIOD
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitDefaultPeriod(t);} }
	,AUDIT
		{ @Override public <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> v, T t) { return v.visitAudit(t);} }
	;
	public abstract <R,T> R visit(AccountingPeriodMetadataVisitor<R,T> visitor, T t);

	public static interface AccountingPeriodMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitName( T t );
		 R visitStartDate( T t );
		 R visitEndDate( T t );
		 R visitStatus( T t );
		 R visitDefaultPeriod( T t );
		 R visitAudit( T t );
	}
}
