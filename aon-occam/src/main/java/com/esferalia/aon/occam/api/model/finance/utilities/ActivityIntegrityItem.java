package com.esferalia.aon.occam.api.model.finance.utilities;

public class ActivityIntegrityItem implements IFinanceUtilitiesItem {
	
	private static final long serialVersionUID = 4569800197470285266L;
	
	private Integer domain;
	private String domainName;
	
	private Integer invoiceId;
	private String invoiceRef;
	private Integer invoiceActivityId;
	private String invoiceActivityRef;
	private boolean invoiceDeclared;
	
	private Integer accountEntryId;
	private Integer accountEntryActivityId;
	private String accountEntryActivityRef;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public ActivityIntegrityItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public ActivityIntegrityItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public FinanceUtilitiesItemType getType() {
		return FinanceUtilitiesItemType.FINANCE_INVOICE_INTEGRITY_CHECK;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public ActivityIntegrityItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public ActivityIntegrityItem setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}
	public String getInvoiceRef() {
		return invoiceRef;
	}
	public ActivityIntegrityItem setInvoiceRef(String invoiceRef) {
		this.invoiceRef = invoiceRef;
		return this;
	}
	public Integer getInvoiceActivityId() {
		return invoiceActivityId;
	}
	public ActivityIntegrityItem setInvoiceActivityId(Integer invoiceActivityId) {
		this.invoiceActivityId = invoiceActivityId;
		return this;
	}
	
	public String getInvoiceActivityRef() {
		return invoiceActivityRef;
	}
	public ActivityIntegrityItem setInvoiceActivityRef(String invoiceActivityRef) {
		this.invoiceActivityRef = invoiceActivityRef;
		return this;
	}
	
	public boolean isInvoiceDeclared() {
		return invoiceDeclared;
	}
	public ActivityIntegrityItem setInvoiceDeclared(boolean invoiceDeclared) {
		this.invoiceDeclared = invoiceDeclared;
		return this;
	}
	
	public Integer getAccountEntryId() {
		return accountEntryId;
	}
	public ActivityIntegrityItem setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
		return this;
	}
	public Integer getAccountEntryActivityId() {
		return accountEntryActivityId;
	}
	public ActivityIntegrityItem setAccountEntryActivityId(Integer accountEntryActivityId) {
		this.accountEntryActivityId = accountEntryActivityId;
		return this;
	}
	public String getAccountEntryActivityRef() {
		return accountEntryActivityRef;
	}
	public ActivityIntegrityItem setAccountEntryActivityRef(String accountEntryActivityRef) {
		this.accountEntryActivityRef = accountEntryActivityRef;
		return this;
	}
	
}
