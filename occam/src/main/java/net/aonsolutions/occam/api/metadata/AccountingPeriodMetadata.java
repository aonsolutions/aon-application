package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum AccountingPeriodMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitDomain();} }
	,NAME
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitName();} }
	,START_DATE
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitStartDate();} }
	,END_DATE
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitEndDate();} }
	,STATUS
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitStatus();} }
	,DEFAULT_PERIOD
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitDefaultPeriod();} }
	,AUDIT
		{ @Override public <R> R visit(AccountingPeriodMetadataVisitor<R> v) { return v.visitAudit();} }
	;
	public abstract <R> R visit(AccountingPeriodMetadataVisitor<R> visitor);

	public static interface AccountingPeriodMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitName();
		 R visitStartDate();
		 R visitEndDate();
		 R visitStatus();
		 R visitDefaultPeriod();
		 R visitAudit();
	}
}
