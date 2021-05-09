package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class Finance implements Serializable, HasAudit {

	private static final long serialVersionUID = -1224312562688230254L;
	
	private boolean dirty;
	private boolean selected;
	private boolean removed;
	
	private Integer id;
	private Invoice invoice;
	private Integer payMethod;
	private PayMethodType payMethodType;
	private String payMethodName;
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
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	private boolean recordable;
	
	public Integer getId() {
		return id;
	}

	public Finance setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public Finance setInvoice(Invoice invoice) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.invoice , invoice) );
		this.invoice = invoice;
		return this;
	}
	public boolean hasInvoice(){
		return getInvoice() != null;
	}
	public boolean isFromSalesInvoice(){
		return hasInvoice() && getInvoice().isSales();
	}
	public boolean isFromExpensesInvoice(){
		return hasInvoice() && getInvoice().isExpenses();
	}
	public boolean isFromPurchaseInvoice(){
		return hasInvoice() && getInvoice().isPurchase();
	}
	public boolean isFromUndeductibleInvoice(){
		return hasInvoice() && getInvoice().isUndeductible();
	}
	
	public Integer getPayMethod() {
		return payMethod;
	}

	public Finance setPayMethod(Integer payMethod) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.payMethod , payMethod) );
		this.payMethod = payMethod;
		return this;
	}

	public String getPayMethodName() {
		return payMethodName;
	}

	public Finance setPayMethodName(String payMethodName) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.payMethodName , payMethodName) );
		this.payMethodName = payMethodName;
		return this;
	}

	public Registry getRegistry() {
		return registry;
	}

	public Finance setRegistry(Registry registry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registry , registry) );
		this.registry = registry;
		return this;
	}

	public Integer getFinanceGroup() {
		return financeGroup;
	}

	public Finance setFinanceGroup(Integer financeGroup) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.financeGroup , financeGroup) );
		this.financeGroup = financeGroup;
		return this;
	}

	public Scope getScope() {
		return scope;
	}

	public Finance setScope(Scope scope) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.scope , scope) );
		this.scope = scope;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Finance setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public boolean isPayment() {
		return payment;
	}

	public Finance setPayment(boolean payment) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.payment , payment) );
		this.payment = payment;
		return this;
	}

	public String getRegistryDocument() {
		return registryDocument;
	}

	public Finance setRegistryDocument(String registryDocument) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryDocument, registryDocument) );
		this.registryDocument = registryDocument;
		return this;
	}

	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}

	public Finance setRegistryDocumentType(DocumentType registryDocumentType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryDocumentType, registryDocumentType) );
		this.registryDocumentType = registryDocumentType;
		return this;
	}

	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}

	public Finance setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryDocumentCountry, registryDocumentCountry) );
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}

	public String getRegistryName() {
		return registryName;
	}

	public Finance setRegistryName(String registryName) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryName, registryName) );
		this.registryName = registryName;
		return this;
	}
	public Integer getRegistryAccountId() {
		return registryAccountId;
	}
	public Finance setRegistryAccountId(Integer registryAccountId) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryAccountId, registryAccountId) );
		this.registryAccountId = registryAccountId;
		return this;
	}
	public String getRegistryAccountCode() {
		return registryAccountCode;
	}
	public Finance setRegistryAccountCode(String registryAccountCode) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryAccountCode, registryAccountCode) );
		this.registryAccountCode = registryAccountCode;
		return this;
	}
	public String getRegistryAccountDescription() {
		return registryAccountDescription;
	}
	public Finance setRegistryAccountDescription(String registryAccountDescription) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registryAccountDescription, registryAccountDescription) );
		this.registryAccountDescription = registryAccountDescription;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public Finance setAmount(double amount) {
		this.setDirty( isDirty()?true: AonUtils.notEquals(this.amount, amount) );
		this.amount = amount;
		return this;
	}

	public double getExpenses() {
		return expenses;
	}

	public Finance setExpenses(double expenses) {
		this.setDirty( isDirty()?true:AonNumberUtils.notEquals(this.expenses, expenses) );
		this.expenses = expenses;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public Finance setConcept(String concept) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.concept, concept) );
		this.concept = concept;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public Finance setDueDate(Date dueDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.dueDate, dueDate) );
		this.dueDate = dueDate;
		return this;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}
	
	public String getBankAccountSafeValue() {
		return bankAccount==null?"":bankAccount.toString();
	}

	public Finance setBankAccount(BankAccount bankAccount) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.bankAccount, bankAccount) );
		this.bankAccount = bankAccount;
		return this;
	}

	public String getBankAlias() {
		return bankAlias;
	}

	public Finance setBankAlias(String bankAlias) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.bankAlias, bankAlias) );
		this.bankAlias = bankAlias;
		return this;
	}

	public String getBic() {
		return bic;
	}

	public Finance setBic(String bic) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.bic, bic) );
		this.bic = bic;
		return this;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public Finance setChequeNumber(String chequeNumber) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.chequeNumber, chequeNumber) );
		this.chequeNumber = chequeNumber;
		return this;
	}

	public FinanceStatus getFinanceStatus() {
		return financeStatus;
	}

	public Finance setFinanceStatus(FinanceStatus financeStatus) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.financeStatus, financeStatus) );
		this.financeStatus = financeStatus;
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

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public Finance setSecurityLevel(SecurityLevel securityLevel) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.securityLevel, securityLevel) );
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Finance setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}

	public String getRemarks() {
		return remarks;
	}

	public Finance setRemarks(String remarks) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.remarks, remarks) );
		this.remarks = remarks;
		return this;
	}

	public boolean isManual() {
		return manual;
	}

	public Finance setManual(boolean manual) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.manual, manual) );
		this.manual = manual;
		return this;
	}

	public boolean isAdvance() {
		return advance;
	}

	public Finance setAdvance(boolean advance) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.advance, advance) );
		this.advance = advance;
		return this;
	}

	public boolean isPayroll() {
		return payroll;
	}

	public Finance setPayroll(boolean payroll) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.payroll , payroll ) );
		this.payroll = payroll;
		return this;
	}

	public boolean isPrepayment() {
		return prepayment;
	}

	public Finance setPrepayment(boolean prepayment) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.prepayment, prepayment) );
		this.prepayment = prepayment;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public Finance setSourceId(Integer sourceId) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.sourceId, sourceId) );
		this.sourceId = sourceId;
		return this;
	}
	
	public PayMethodType getPayMethodType() {
		return payMethodType;
	}

	public Finance setPayMethodType(PayMethodType payMethodType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.payMethodType, payMethodType) );
		this.payMethodType = payMethodType;
		return this;
	}

	// ---------------------------------------------------------- DIRTY
	public boolean isDirty() {
		return dirty;
	}
	public Finance setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	// ---------------------------------------------------------- AUDIT
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
	// ---------------------------------------------------------- 

	
	public boolean isRemoved() {
		return removed;
	}
	public Finance setRemoved(boolean removed) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.removed, removed) );
		this.removed = removed;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}
	public Finance setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	public boolean isEmptyInvoice() {
		return (getInvoice() == null || getInvoice().getId() == null);
	}

	public boolean isRecordable() {
		return recordable;
	}
	public Finance setRecordable(boolean recordable) {
		this.recordable = recordable;
		return this;
	}

}
