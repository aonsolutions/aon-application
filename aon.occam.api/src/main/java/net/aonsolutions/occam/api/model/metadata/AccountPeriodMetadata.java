package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum AccountPeriodMetadata implements Serializable {
	 ID { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitName();} }
	,INITIATION_DATE { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitInitiationDate();} }
	,DEADLINE { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitDeadline();} }
	,STATUS { @Override public <T> T visit(AccountPeriodMetadataVisitor<T> v) { return v.visitStatus();} }
	,	;

	public abstract <T> T visit(AccountPeriodMetadataVisitor<T> v);
	public static interface AccountPeriodMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitInitiationDate();
		T visitDeadline();
		T visitStatus();
	}
}
