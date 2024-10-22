package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.FinanceMetadata;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.FinanceStatus;
import net.aonsolutions.occam.api.model.type.FinanceType;

public class Finance extends AonEntity<FinanceMetadata> implements HasAudit {

	private static final long serialVersionUID = -1224312562688230254L;
	
	private Integer id;
	private Integer domain;
	private Integer scope;
	private FinanceType financeType;
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Account registryAccount;
	private double amount;
	private double expenses;
	private String concept;
	private InvoiceHeader invoice;
	private Date dueDate;
	private PayMethod payMethod;
	private BankAccount bankAccount;
	private String bankAlias;
	private String bic;
	private String chequeNumber;
	private FinanceStatus financeStatus;
	private boolean confidential;
	private String remarks;
	private boolean manual;
	private boolean advance;
	private boolean payroll;
	private boolean prepayment;
	private Integer sourceId;
	private Integer financeGroup;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;

	private Date paidDate;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Finance markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Finance setSelected(boolean selected) {
		super.setSelected(selected);
		return this;
	}
	@Override
	public Finance setDeleted(boolean deleted) {
		super.setSelected(deleted);
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Finance setId(Integer id) {
		checkIfDirty( this.id,id, FinanceMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Finance setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, FinanceMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	
	public FinanceType getFinanceType() {
		return financeType;
	}
	public Finance setFinanceType(FinanceType financeType) {
		checkIfDirty( this.financeType,financeType, FinanceMetadata.TYPE);
		this.financeType = financeType;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Finance setRegistry(Integer registry) {
		checkIfDirty( this.registry,registry, FinanceMetadata.REGISTRY);
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Finance setRegistryDocument(String registryDocument) {
		checkIfDirty( this.registryDocument,registryDocument, FinanceMetadata.RDOCUMENT );
		this.registryDocument = registryDocument;
		return this;
	}

	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Finance setRegistryDocumentType(DocumentType registryDocumentType) {
		checkIfDirty( this.registryDocumentType,registryDocumentType, FinanceMetadata.RDOCUMENT_TYPE );
		this.registryDocumentType = registryDocumentType;
		return this;
	}

	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Finance setRegistryDocumentCountry(Country registryDocumentCountry) {
		checkIfDirty( this.registryDocumentCountry,registryDocumentCountry, FinanceMetadata.RDOCUMENT_COUNTRY );
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	
	public String getRegistryName() {
		return registryName;
	}
	public Finance setRegistryName(String registryName) {
		checkIfDirty( this.registryName,registryName, FinanceMetadata.RNAME );
		this.registryName = registryName;
		return this;
	}

	public Optional<Account> getRegistryAccount() {
		return Optional.ofNullable(registryAccount);
	}
	public Finance setRegistryAccount(Account registryAccount) {
		checkIfDirty( this.registryAccount,registryAccount, FinanceMetadata.REGISTRY_ACCOUNT);
		this.registryAccount = registryAccount;
		return this;
	}
	
	public double getAmount() {
		return amount;
	}
	public Finance setAmount(double amount) {
		checkIfDirty( this.amount,amount, FinanceMetadata.AMOUNT);
		this.amount = amount;
		return this;
	}
	
	public double getExpenses() {
		return expenses;
	}
	public Finance setExpenses(double expenses) {
		checkIfDirty( this.expenses,expenses, FinanceMetadata.EXPENSES);
		this.expenses = expenses;
		return this;
	}
	
	public String getConcept() {
		return concept;
	}
	public Finance setConcept(String concept) {
		checkIfDirty( this.concept,concept, FinanceMetadata.CONCEPT);
		this.concept = concept;
		return this;
	}
	
	public Optional<InvoiceHeader> getInvoice() {
		return Optional.ofNullable(invoice);
	}
	public Finance setInvoice(InvoiceHeader invoice) {
		checkIfDirty( this.invoice,invoice, FinanceMetadata.INVOICE);
		this.invoice = invoice;
		return this;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	public Finance setDueDate(Date dueDate) {
		checkIfDirty( this.dueDate,dueDate, FinanceMetadata.DUE_DATE);
		this.dueDate = dueDate;
		return this;
	}
	
	public Optional<PayMethod> getPayMethod() {
		return Optional.ofNullable(payMethod);
	}
	public Finance setPayMethod(PayMethod payMethod) {
		checkIfDirty( this.payMethod,payMethod, FinanceMetadata.PAY_METHOD);
		this.payMethod = payMethod;
		return this;
	}
	
	public Optional<BankAccount> getBankAccount() {
		return Optional.ofNullable(bankAccount);
	}
	public Finance setBankAccount(BankAccount bankAccount) {
		checkIfDirty( this.bankAccount,bankAccount, FinanceMetadata.BANK_ACCOUNT);
		this.bankAccount = bankAccount;
		return this;
	}
	
	public String getBankAlias() {
		return bankAlias;
	}
	public Finance setBankAlias(String bankAlias) {
		checkIfDirty( this.bankAlias,bankAlias, FinanceMetadata.BANK_ALIAS);
		this.bankAlias = bankAlias;
		return this;
	}

	public String getBic() {
		return bic;
	}
	public Finance setBic(String bic) {
		checkIfDirty( this.bic,bic, FinanceMetadata.BIC);
		this.bic = bic;
		return this;
	}
	
	public String getChequeNumber() {
		return chequeNumber;
	}
	public Finance setChequeNumber(String chequeNumber) {
		checkIfDirty( this.chequeNumber,chequeNumber, FinanceMetadata.CHEQUE_NUMBER);
		this.chequeNumber = chequeNumber;
		return this;
	}

	public FinanceStatus getFinanceStatus() {
		return financeStatus;
	}
	public Finance setFinanceStatus(FinanceStatus financeStatus) {
		checkIfDirty( this.financeStatus,financeStatus, FinanceMetadata.STATUS);
		this.financeStatus = financeStatus;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Finance setConfidential(boolean confidential) {
		checkIfDirty( this.confidential,confidential, FinanceMetadata.SECURITY_LEVEL);
		this.confidential = confidential;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}
	public Finance setRemarks(String remarks) {
		checkIfDirty( this.remarks,remarks, FinanceMetadata.REMARKS);
		this.remarks = remarks;
		return this;
	}

	public Integer getScope() {
		return scope;
	}
	public Finance setScope(Integer scope) {
		checkIfDirty( this.scope,scope, FinanceMetadata.SCOPE);
		this.scope = scope;
		return this;
	}
	
	public boolean isManual() {
		return manual;
	}
	public Finance setManual(boolean manual) {
		checkIfDirty( this.manual,manual, FinanceMetadata.MANUAL);
		this.manual = manual;
		return this;
	}

	public boolean isAdvance() {
		return advance;
	}
	public Finance setAdvance(boolean advance) {
		checkIfDirty( this.advance,advance, FinanceMetadata.ADVANCE);
		this.advance = advance;
		return this;
	}

	public boolean isPayroll() {
		return payroll;
	}
	public Finance setPayroll(boolean payroll) {
		checkIfDirty( this.payroll,payroll, FinanceMetadata.PAYROLL);
		this.payroll = payroll;
		return this;
	}

	public boolean isPrepayment() {
		return prepayment;
	}
	public Finance setPrepayment(boolean prepayment) {
		checkIfDirty( this.prepayment,prepayment, FinanceMetadata.PREPAYMENT);
		this.prepayment = prepayment;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}
	public Finance setSourceId(Integer sourceId) {
		checkIfDirty( this.sourceId,sourceId, FinanceMetadata.SOURCE_ID);
		this.sourceId = sourceId;
		return this;
	}
	
	public Integer getFinanceGroup() {
		return financeGroup;
	}
	public Finance setFinanceGroup(Integer financeGroup) {
		checkIfDirty( this.financeGroup,financeGroup, FinanceMetadata.FINANCE_GROUP);
		this.financeGroup = financeGroup;
		return this;
	}
	
	public Optional<Date> getPaidDate() {
		return Optional.ofNullable(paidDate);
	}
	public Finance setPaidDate(Date paidDate) {
		checkIfDirty( this.paidDate,paidDate, FinanceMetadata.PAID_DATE);
		this.paidDate = paidDate;
		return this;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Finance setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Finance setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Finance setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Finance setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Finance other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}

	public boolean isFullPending() {
		return getFinanceStatus() == null || FinanceStatus.PENDING == getFinanceStatus() || FinanceStatus.RETURNED == getFinanceStatus();
	}
	public boolean isPending() {
		return FinanceStatus.PENDING == getFinanceStatus();
	}
	public boolean isSettled() {
		return FinanceStatus.SETTLED == getFinanceStatus();
	}
	public boolean isReturned() {
		return FinanceStatus.RETURNED == getFinanceStatus();
	}
	public boolean isPaid() {
		return FinanceStatus.PAID == getFinanceStatus();
	}
	public boolean isBatched() {
		return FinanceStatus.BATCHED == getFinanceStatus();
	}

}
