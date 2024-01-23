package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

public class InvoiceBatch implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private Date date;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationOperation operation;
	private Integer dataResponse;
	private String creationUser;
	
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
	
	public Date getDate() {
		return date;
	}
	
	public InvoiceBatch setDate(Date date) {
		this.date = date;
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

	public boolean isEmpty() {
		return getId() == null && getDomain() == null && getDate() == null 
			&& getType() == null && getOperation() == null && getDataResponse() == null;
	}
}
