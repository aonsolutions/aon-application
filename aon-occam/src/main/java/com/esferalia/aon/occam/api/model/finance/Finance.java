package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class Finance implements Serializable, HasAudit {

	private static final long serialVersionUID = -1224312562688230254L;

	private boolean checked;
	
	private Integer id;
	private Invoice invoice;
	private Integer payMethod;
	private Registry registry;
	private Scope scope;
	private Integer domain;
	private boolean payment;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Integer registryAccountId;
	private String registryAccountCode;
	private String registryAccountDescription;
	private double amount;
	private double expenses;
	private String concept;
	private Date dueDate;
	private BankAccount bankAccount;
	private String bankAlias;
	private String bic;
	private String chequeNumber;
	private FinanceStatus financeStatus;
	private SecurityLevel securityLevel;
	private String remarks;
	private boolean manual;
	private boolean advance;
	private boolean payroll;
	private boolean prepayment;
	private Integer sourceId;
	private Integer financeGroup;
	
	private Integer payAccountId;
	private String payAccountCode;
	private String payAccountDescription;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	public Integer getId() {
		return id;
	}

	public Finance setId(Integer id) {
		this.id = id;
		return this;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public Finance setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	public boolean isFromSalesInvoice(){
		return getInvoice() != null && getInvoice().isSales();
	}
	public boolean isFromExpensesInvoice(){
		return getInvoice() != null && getInvoice().isExpenses();
	}
	public boolean isFromPurchaseInvoice(){
		return getInvoice() != null && getInvoice().isPurchase();
	}
	public boolean isFromUndeductibleInvoice(){
		return getInvoice() != null && getInvoice().isUndeductible();
	}
	
	public Integer getPayMethod() {
		return payMethod;
	}

	public Finance setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}

	public Registry getRegistry() {
		return registry;
	}

	public Finance setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}

	public Integer getFinanceGroup() {
		return financeGroup;
	}

	public Finance setFinanceGroup(Integer financeGroup) {
		this.financeGroup = financeGroup;
		return this;
	}

	public Scope getScope() {
		return scope;
	}

	public Finance setScope(Scope scope) {
		this.scope = scope;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Finance setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public boolean isPayment() {
		return payment;
	}

	public Finance setPayment(boolean payment) {
		this.payment = payment;
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
	public Integer getRegistryAccountId() {
		return registryAccountId;
	}
	public Finance setRegistryAccountId(Integer registryAccountId) {
		this.registryAccountId = registryAccountId;
		return this;
	}
	public String getRegistryAccountCode() {
		return registryAccountCode;
	}
	public Finance setRegistryAccountCode(String registryAccountCode) {
		this.registryAccountCode = registryAccountCode;
		return this;
	}
	public String getRegistryAccountDescription() {
		return registryAccountDescription;
	}
	public Finance setRegistryAccountDescription(String registryAccountDescription) {
		this.registryAccountDescription = registryAccountDescription;
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

	public Date getDueDate() {
		return dueDate;
	}

	public Finance setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
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
	
	public boolean isPending() {
		return FinanceStatus.PENDING == getFinanceStatus();
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public Finance setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}

	public Finance setRemarks(String remarks) {
		this.remarks = remarks;
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
	
	public Integer getPayAccountId() {
		return payAccountId;
	}
	public Finance setPayAccountId(Integer payAccountId) {
		this.payAccountId = payAccountId;
		return this;
	}
	public String getPayAccountCode() {
		return payAccountCode;
	}
	public Finance setPayAccountCode(String payAccountCode) {
		this.payAccountCode = payAccountCode;
		return this;
	}
	public String getPayAccountDescription() {
		return payAccountDescription;
	}
	public Finance setPayAccountDescription(String payAccountDescription) {
		this.payAccountDescription = payAccountDescription;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Finance setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Finance setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public Finance setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Finance setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Finance setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	public boolean isEmptyInvoice() {
		return (getInvoice() == null || getInvoice().getId() == null);
	}
	
	public boolean isChecked() {
		return checked;
	}
	public Finance setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}
	
}
