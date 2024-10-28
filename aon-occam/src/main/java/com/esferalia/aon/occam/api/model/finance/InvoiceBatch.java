package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

public class InvoiceBatch implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String description;
	private Date date;
	private Date endDate;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationOperation operation;
	private Integer dataResponse;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	
	public InvoiceBatch setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public InvoiceBatch setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public InvoiceBatch setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	
	public InvoiceBatch setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public InvoiceBatch setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public InvoiceCommunicationType getType() {
		return type;
	}
	
	public InvoiceBatch setType(InvoiceCommunicationType type) {
		this.type = type;
		return this;
	}
	
	public InvoiceCommunicationOperation getOperation() {
		return operation;
	}
	
	public InvoiceBatch setOperation(InvoiceCommunicationOperation operation) {
		this.operation = operation;
		return this;
	}
	
	public Integer getDataResponse() {
		return dataResponse;
	}

	public InvoiceBatch setDataResponse(Integer dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public InvoiceBatch setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public InvoiceBatch setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public InvoiceBatch setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
	
	public InvoiceBatch setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getDate() == null 
			&& getType() == null && getOperation() == null && getDataResponse() == null;
	}
}
