package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementReliability;
import com.esferalia.aon.occam.api.model.type.StatementStatus;


public class BankStatement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2154568050977394984L;
	private Integer id;
	private RegistryBank registryBank;
	private int domain;
	private int lotNumber;
	private Date operationDate;
	private StatementConcept commonConcept;
	private String ownConcept;
	private boolean payment;
	private double amount;
	private String document;
	private String reference1;
	private String reference2;
	private String description;
	private StatementReliability reliability;
	private SecurityLevel securityLevel;
	private StatementStatus status;
	private String comments;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getLotNumber() {
		return lotNumber;
	}
	public void setLotNumber(int lotNumber) {
		this.lotNumber = lotNumber;
	}
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	public StatementConcept getCommonConcept() {
		return commonConcept;
	}
	public void setCommonConcept(StatementConcept commonConcept) {
		this.commonConcept = commonConcept;
	}
	public String getOwnConcept() {
		return ownConcept;
	}
	public void setOwnConcept(String ownConcept) {
		this.ownConcept = ownConcept;
	}
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getReference1() {
		return reference1;
	}
	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}
	public String getReference2() {
		return reference2;
	}
	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public StatementReliability getReliability() {
		return reliability;
	}
	public void setReliability(StatementReliability reliability) {
		this.reliability = reliability;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public StatementStatus getStatus() {
		return status;
	}
	public void setStatus(StatementStatus status) {
		this.status = status;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
