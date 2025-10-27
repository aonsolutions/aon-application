package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;

public class InvoiceBatchDetail implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Integer invoice;
	private Integer invoiceBatch;
	private InvoiceCommunicationStatus status;
	
	public Integer getId() {
		return id;
	}
	
	public InvoiceBatchDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public InvoiceBatchDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	
	public InvoiceBatchDetail setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public Integer getInvoiceBatch() {
		return invoiceBatch;
	}
	
	public InvoiceBatchDetail setInvoiceBatch(Integer invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
		return this;
	}

	public InvoiceCommunicationStatus getStatus() {
		return status;
	}
	
	public InvoiceBatchDetail setStatus(InvoiceCommunicationStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getInvoice() == null 
				&& getInvoiceBatch() == null && getStatus() == null;
	}
	
}
