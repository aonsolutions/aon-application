package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.FinanceStatus;
import net.aonsolutions.occam.api.model.type.FinanceType;

public class Finance implements Serializable, HasAudit {

	private static final long serialVersionUID = -1224312562688230254L;
	
	private boolean selected;
	private boolean deleted;
	
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

	public boolean isSelected() {
		return selected;
	}
	public Finance setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public Finance setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Finance setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Finance setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	
	public FinanceType getFinanceType() {
		return financeType;
	}
	public Finance setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
		return this;
	}
//	public boolean isPayment() {
//		return getFinanceType() == FinanceType.PAYMENT;
//	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Finance setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Finance setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}

	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Finance setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}

	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Finance setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	
	public String getRegistryName() {
		return registryName;
	}
	public Finance setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public Optional<Account> getRegistryAccount() {
		return Optional.ofNullable(registryAccount);
	}
	public Finance setRegistryAccount(Account registryAccount) {
		this.registryAccount = registryAccount;
		return this;
	}
	
	public double getAmount() {
		return amount;
	}
	public Finance setAmount(double amount) {
		
		this.amount = amount;
		return this;
	}
	
	public double getExpenses() {
		return expenses;
	}
	public Finance setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}
	
	public String getConcept() {
		return concept;
	}
	public Finance setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	
	public Optional<InvoiceHeader> getInvoice() {
		return Optional.ofNullable(invoice);
	}
	public Finance setInvoice(InvoiceHeader invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	public Finance setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}
	
	public Optional<PayMethod> getPayMethod() {
		return Optional.ofNullable(payMethod);
	}
	public Finance setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	
	public Optional<BankAccount> getBankAccount() {
		return Optional.ofNullable(bankAccount);
	}
	public Finance setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	public String getBankAlias() {
		return bankAlias;
	}
	public Finance setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}

	public String getBic() {
		return bic;
	}
	public Finance setBic(String bic) {
		this.bic = bic;
		return this;
	}
	
	public String getChequeNumber() {
		return chequeNumber;
	}
	public Finance setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
		return this;
	}

	public FinanceStatus getFinanceStatus() {
		return financeStatus;
	}
	public Finance setFinanceStatus(FinanceStatus financeStatus) {
		this.financeStatus = financeStatus;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Finance setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}
	public Finance setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}

	public Integer getScope() {
		return scope;
	}
	public Finance setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public boolean isManual() {
		return manual;
	}
	public Finance setManual(boolean manual) {
		this.manual = manual;
		return this;
	}

	public boolean isAdvance() {
		return advance;
	}
	public Finance setAdvance(boolean advance) {
		this.advance = advance;
		return this;
	}

	public boolean isPayroll() {
		return payroll;
	}
	public Finance setPayroll(boolean payroll) {
		this.payroll = payroll;
		return this;
	}

	public boolean isPrepayment() {
		return prepayment;
	}
	public Finance setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}
	public Finance setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
	public Integer getFinanceGroup() {
		return financeGroup;
	}
	public Finance setFinanceGroup(Integer financeGroup) {
		this.financeGroup = financeGroup;
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

	public Optional<Date> getPaidDate() {
		return Optional.ofNullable(paidDate);
	}
	public Finance setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
		return this;
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
