package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum FinanceMetadata implements Serializable {
	 ID { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitDomain();} }
	,TYPE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitType();} }
	,REGISTRY { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRegistry();} }
	,RDOCUMENT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRegistryDocument();} }
	,RDOCUMENT_TYPE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRegistryDocumentType();} }
	,RDOCUMENT_COUNTRY { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRegistryDocumentCountry();} }
	,REGISTRY_ACCOUNT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRegistryAccount();} }
	,RNAME { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRname();} }
	,AMOUNT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitAmount();} }
	,EXPENSES { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitExpenses();} }
	,CONCEPT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitConcept();} }
	,INVOICE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitInvoice();} }
	,DUE_DATE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitDueDate();} }
	,PAY_METHOD { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitPayMethod();} }
	,BANK_ACCOUNT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitBankAccount();} }
	,BANK_ALIAS { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitBankAlias();} }
	,BIC { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitBic();} }
	,CHEQUE_NUMBER { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitChequeNumber();} }
	,STATUS { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitStatus();} }
	,SECURITY_LEVEL { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitSecurityLevel();} }
	,REMARKS { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitRemarks();} }
	,SCOPE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitScope();} }
	,MANUAL { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitManual();} }
	,ADVANCE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitAdvance();} }
	,PAYROLL { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitPayroll();} }
	,PREPAYMENT { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitPrepayment();} }
	,SOURCE_ID { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitSourceId();} }
	,FINANCE_GROUP { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitFinanceGroup();} }
	,PAID_DATE { @Override public <T> T visit(FinanceMetadataVisitor<T> v) { return v.visitPaidDate();} }
	;

	public abstract <T> T visit(FinanceMetadataVisitor<T> v);
	public static interface FinanceMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitType();
		T visitRegistry();
		T visitRegistryDocument();
		T visitRegistryDocumentType();
		T visitRegistryDocumentCountry();
		T visitRegistryAccount();
		T visitRname();
		T visitAmount();
		T visitExpenses();
		T visitConcept();
		T visitInvoice();
		T visitDueDate();
		T visitPayMethod();
		T visitBankAccount();
		T visitBankAlias();
		T visitBic();
		T visitChequeNumber();
		T visitStatus();
		T visitSecurityLevel();
		T visitRemarks();
		T visitScope();
		T visitManual();
		T visitAdvance();
		T visitPayroll();
		T visitPrepayment();
		T visitSourceId();
		T visitFinanceGroup();
		T visitPaidDate();
	}
}
