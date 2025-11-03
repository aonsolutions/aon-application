package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;

public class InvoiceInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationStatus status;
	
	private String checkUrl;
	
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
	
	public InvoiceCommunicationStatus getStatus() {
		return status;
	}
	public InvoiceInfo setStatus(InvoiceCommunicationStatus status) {
		this.status = status;
		return this;
	}
	public boolean isPending() 				{ return getStatus() == null || (getStatus() != null && getStatus().isPending());}
	public boolean isAccepted() 			{ return getStatus() != null && getStatus().isAccepted();}
	public boolean isAcceptedWithErrors() 	{ return getStatus() != null && getStatus().isAcceptedWithErrors();}
	public boolean isWrong() 				{ return getStatus() != null && getStatus().isWrong();}
	public boolean isAnnulled() 			{ return getStatus() != null && getStatus().isAnnulled();}
	public boolean isPartialAccepted() 		{ return isAccepted() || isAcceptedWithErrors();}
	
	public String getCheckUrl() {
		return checkUrl;
	}
	public InvoiceInfo setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
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
	
}
