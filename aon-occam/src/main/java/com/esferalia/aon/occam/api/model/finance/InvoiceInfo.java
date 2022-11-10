package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvoiceInfo implements Serializable {
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationStatus status;

	public Integer getId() {
		return id;
	}

	public InvoiceInfo setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public InvoiceInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getInvoice() {
		return invoice;
	}

	public InvoiceInfo setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}

	public InvoiceCommunicationType getType() {
		return type;
	}

	public InvoiceInfo setType(InvoiceCommunicationType type) {
		this.type = type;
		return this;
	}
	
	public boolean isAccepted() {
		return InvoiceCommunicationStatus.ACCEPTED.equals(getStatus());
	}
	
	public boolean isAcceptedWithErrors() {
		return InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS.equals(getStatus());
	}

	public boolean isWrong() {
		return getStatus().isWrong();
	}
	
	public boolean isAnnuled() {
		return InvoiceCommunicationStatus.ANNULLED.equals(getStatus());
	}

	public InvoiceCommunicationStatus getStatus() {
		if(status == null) {
			this.status = InvoiceCommunicationStatus.PENDING;
		}
		return status;
	}

	public InvoiceInfo setStatus(InvoiceCommunicationStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getInvoice() == null
				&& getType() == null;		
	}
}
