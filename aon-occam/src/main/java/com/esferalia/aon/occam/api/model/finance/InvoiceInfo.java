package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

public class InvoiceInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationStatus status;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

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
		return InvoiceCommunicationStatus.CANCELLED.equals(getStatus());
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
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public InvoiceInfo setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public InvoiceInfo setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public InvoiceInfo setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public InvoiceInfo setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getInvoice() == null
				&& getType() == null;		
	}
}
