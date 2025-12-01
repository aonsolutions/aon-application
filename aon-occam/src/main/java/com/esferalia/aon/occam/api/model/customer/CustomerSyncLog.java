package com.esferalia.aon.occam.api.model.customer;

import java.io.Serializable;

public class CustomerSyncLog implements Serializable {

	private static final long serialVersionUID = 7625010032347396755L;
	
	private String customerId = "";
	private String customerName = "";
	private String customerDocument = "";
	private String enterpriseId = "";
	private String enterpriseName = "";
	private String enterpriseDocument = "";
	private String messageType = ""; // ÉXITO, WARNING, ERROR
	private String message = "";
	
	public CustomerSyncLog() {
		super();
	}

	public String getCustomerId() {
		return customerId;
	}

	public CustomerSyncLog setCustomerId(String customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getCustomerName() {
		return customerName;
	}

	public CustomerSyncLog setCustomerName(String customerName) {
		this.customerName = customerName;
		return this;
	}

	public String getCustomerDocument() {
		return customerDocument;
	}

	public CustomerSyncLog setCustomerDocument(String customerDocument) {
		this.customerDocument = customerDocument;
		return this;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public CustomerSyncLog setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
		return this;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public CustomerSyncLog setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public CustomerSyncLog setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}

	public String getMessageType() {
		return messageType;
	}

	public CustomerSyncLog setMessageType(String messageType) {
		this.messageType = messageType;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public CustomerSyncLog setMessage(String message) {
		this.message = message;
		return this;
	}
	
}
