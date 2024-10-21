package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum AccountEntryMetadata implements Serializable {
	 ID { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitDomain();} }
	,ACCOUNT_PERIOD { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitAccountPeriod();} }
	,ACTIVITY { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitActivity();} }
	,ENTRY_DATE { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitEntryDate();} }
	,ENTRY_TYPE { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitEntryType();} }
	,JOURNAL { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitJournal();} }
	,SECURITY_LEVEL { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitSecurityLevel();} }
	,COMMENTS { @Override public <T> T visit(AccountEntryMetadataVisitor<T> v) { return v.visitComments();} }
	,	;

	public abstract <T> T visit(AccountEntryMetadataVisitor<T> v);
	public static interface AccountEntryMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitAccountPeriod();
		T visitActivity();
		T visitEntryDate();
		T visitEntryType();
		T visitJournal();
		T visitSecurityLevel();
		T visitComments();
	}
}
