package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementReliability;
import com.esferalia.aon.occam.api.model.type.StatementStatus;


public class BankStatement implements Serializable {

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
	public BankStatement setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public BankStatement setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
		return this;

	}
	public int getDomain() {
		return domain;
	}
	public BankStatement setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public int getLotNumber() {
		return lotNumber;
	}
	public BankStatement setLotNumber(int lotNumber) {
		this.lotNumber = lotNumber;
		return this;
	}
	
	public Date getOperationDate() {
		return operationDate;
	}
	public BankStatement setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
		return this;
	}
	
	public StatementConcept getCommonConcept() {
		return commonConcept;
	}
	public BankStatement setCommonConcept(StatementConcept commonConcept) {
		this.commonConcept = commonConcept;
		return this;
	}
	
	public String getOwnConcept() {
		return ownConcept;
	}
	public BankStatement setOwnConcept(String ownConcept) {
		this.ownConcept = ownConcept;
		return this;
	}
	
	public boolean isPayment() {
		return payment;
	}
	public BankStatement setPayment(boolean payment) {
		this.payment = payment;
		return this;
	}
	
	public double getAmount() {
		return amount;
	}
	public BankStatement setAmount(double amount) {
		this.amount = amount;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public BankStatement setDocument(String document) {
		this.document = document;
		return this;		
	}
	
	public String getReference1() {
		return reference1;
	}
	public BankStatement setReference1(String reference1) {
		this.reference1 = reference1;
		return this;
	}
	
	public String getReference2() {
		return reference2;
	}
	public BankStatement setReference2(String reference2) {
		this.reference2 = reference2;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public BankStatement setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public StatementReliability getReliability() {
		return reliability;
	}
	public BankStatement setReliability(StatementReliability reliability) {
		this.reliability = reliability;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public BankStatement setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public StatementStatus getStatus() {
		return status;
	}
	public BankStatement setStatus(StatementStatus status) {
		this.status = status;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public BankStatement setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	
}
