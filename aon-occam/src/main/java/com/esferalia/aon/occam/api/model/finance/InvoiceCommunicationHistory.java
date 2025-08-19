package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

public class InvoiceCommunicationHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	Date date;
	InvoiceCommunicationOperation operation;
	InvoiceCommunicationStatus status;
	String requestUrl;
	String responseUrl;

	public Date getDate() {
		return date;
	}
	
	public InvoiceCommunicationHistory setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public InvoiceCommunicationOperation getOperation() {
		return operation;
	}
	
	public InvoiceCommunicationHistory setOperation(InvoiceCommunicationOperation operation) {
		this.operation = operation;
		return this;
	}
	
	public InvoiceCommunicationStatus getStatus() {
		return status;
	}
	
	public InvoiceCommunicationHistory setStatus(InvoiceCommunicationStatus status) {
		this.status = status;
		return this;
	}
	
	public String getRequestUrl() {
		return requestUrl;
	}
	
	public InvoiceCommunicationHistory setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}
	
	public String getResponseUrl() {
		return responseUrl;
	}
	
	public InvoiceCommunicationHistory setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
		return this;
	}
}
