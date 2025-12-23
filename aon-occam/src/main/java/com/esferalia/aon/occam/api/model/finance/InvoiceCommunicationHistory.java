package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;

public class InvoiceCommunicationHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer invoiceId;
	private Date date;
	private String creationUser;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationOperation operation;
	private InvoiceCommunicationStatus status;
	private String requestUrl;
	private String responseUrl;
	private byte[] responseData;
	private LinkedList<String> responseMessages;
	
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public InvoiceCommunicationHistory setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	public InvoiceCommunicationHistory setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	public InvoiceCommunicationHistory setCreationUser(String value) {
		this.creationUser = value;
		return this;
	}
	
	public InvoiceCommunicationType getType() {
		return type;
	}
	public InvoiceCommunicationHistory setType(InvoiceCommunicationType type) {
		this.type = type;
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
	public boolean isWrong() {
		return this.status == InvoiceCommunicationStatus.WRONG;
	}
	public boolean isAcceptedWithErrors() {
		return this.status == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;
	}
	public boolean isAccepted() {
		return this.status == InvoiceCommunicationStatus.ACCEPTED;
	}
	public boolean isPending() {
		return this.status == InvoiceCommunicationStatus.PENDING;
	}
	
	public boolean isMessagesVisible() {
		return isWrong() || isAcceptedWithErrors();
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
	
	public byte[] getResponseData() {
		return responseData;
	}
	public InvoiceCommunicationHistory setResponseData(byte[] responseData) {
		this.responseData = responseData;
		return this;
	}
	
	public LinkedList<String> getResponseMessages() {
		return responseMessages;
	}
	public InvoiceCommunicationHistory setResponseMessages(LinkedList<String> messages) {
		this.responseMessages = messages;
		return this;
	}
}

