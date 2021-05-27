package com.esferalia.aon.occam.api.model.finance.utilities;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public class MissingFinanceInvoiceItem implements IFinanceUtilitiesItem {
	
	private static final long serialVersionUID = 4569800197470285266L;
	
	private Integer domain;
	private String domainName;
	
	private Invoice invoice;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public MissingFinanceInvoiceItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public MissingFinanceInvoiceItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public FinanceUtilitiesItemType getType() {
		return FinanceUtilitiesItemType.MISSING_FINANCE_INVOICE;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public MissingFinanceInvoiceItem setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public MissingFinanceInvoiceItem setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
}
