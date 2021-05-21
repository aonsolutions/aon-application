package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public class AccUtilitiesInvoiceIntegrityItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Invoice invoice;
	private boolean customer;
	private boolean creditor;
	private boolean supplier;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesInvoiceIntegrityItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesInvoiceIntegrityItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.INVOICE_INTEGRITY;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesInvoiceIntegrityItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public AccUtilitiesInvoiceIntegrityItem setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public boolean isCustomer() {
		return customer;
	}
	public AccUtilitiesInvoiceIntegrityItem setCustomer(boolean customer) {
		this.customer = customer;
		return this;
	}
	
	public boolean isCreditor() {
		return creditor;
	}
	public AccUtilitiesInvoiceIntegrityItem setCreditor(boolean creditor) {
		this.creditor = creditor;
		return this;
	}
	
	public boolean isSupplier() {
		return supplier;
	}
	public AccUtilitiesInvoiceIntegrityItem setSupplier(boolean supplier) {
		this.supplier = supplier;
		return this;
	}
	
}
