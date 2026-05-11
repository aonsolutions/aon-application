package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public class AmortizationInvoice implements Serializable {
	
	private static final long serialVersionUID = 2059322794384576856L;
	
	private Integer id;
	private Integer domain;
	private Amortization amortization;
	private Invoice invoice;
	private Integer accountEntryId;
	
	public Integer getId() {
		return id;
	}
	public AmortizationInvoice setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public AmortizationInvoice setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Amortization getAmortization() {
		return amortization;
	}
	public AmortizationInvoice setAmortization(Amortization amortization) {
		this.amortization = amortization;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public AmortizationInvoice setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public Integer getAccountEntryId() {
		return accountEntryId;
	}
	public AmortizationInvoice setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
		return this;
	}
	
}
