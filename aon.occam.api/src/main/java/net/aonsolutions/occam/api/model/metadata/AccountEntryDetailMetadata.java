package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum AccountEntryDetailMetadata implements Serializable {
	ID { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitDomain();} }
	,ACCOUNT_ENTRY { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitAccountEntry();} }
	,LINE { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitLine();} }
	,ACCOUNT { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitAccount();} }
	,CONCEPT { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitConcept();} }
	,BALANCING_ACCOUNT { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitBalancingAccount();} }
	,DEBIT { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitDebit();} }
	,CREDIT { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitCredit();} }
	,DOCUMENT_NUMBER { @Override public <T> T visit(AccountEntryDetailMetadataVisitor<T> v) { return v.visitDocumentNumber();} }
	,	;

	public abstract <T> T visit(AccountEntryDetailMetadataVisitor<T> v);
	public static interface AccountEntryDetailMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitAccountEntry();
		T visitLine();
		T visitAccount();
		T visitConcept();
		T visitBalancingAccount();
		T visitDebit();
		T visitCredit();
		T visitDocumentNumber();
	}
}
