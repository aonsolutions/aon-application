package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class FinanceParams implements Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private int domain;
	private String domainName;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private Date fromDueDate;
	private Date toDueDate;
	private SecurityLevel securityLevel; 
	private boolean hasConfidentialityRole;
	private Boolean payment;
	
	private boolean pending;
	private boolean batched;
	private boolean returned;
	private boolean paid;
	private boolean settled;
	
	private Integer registry;
	private Double amount;
	private boolean nearbyNumbers;
	private double factor = 5;
	private String concept;
	private String referenceCode;
	private Integer payMethod;
	private int order;
	
	private boolean isPayroll;
	
	public int getDomain() {
		return domain;
	}
	public FinanceParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getDomainName() {
		return domainName;
	}
	public FinanceParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public FinanceParams setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
		return this;
	}
	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public FinanceParams setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
		return this;
	}
	public Date getFromDueDate() {
		return fromDueDate;
	}
	public FinanceParams setFromDueDate(Date fromDueDate) {
		this.fromDueDate = fromDueDate;
		return this;
	}
	public Date getToDueDate() {
		return toDueDate;
	}
	public FinanceParams setToDueDate(Date toDueDate) {
		this.toDueDate = toDueDate;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public FinanceParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Double getAmount() {
		return amount;
	}
	public FinanceParams setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	public boolean isNearbyNumbers() {
		return nearbyNumbers;
	}
	public FinanceParams setNearbyNumbers(boolean nearbyNumbers) {
		this.nearbyNumbers = nearbyNumbers;
		return this;
	}
	public double getFactor() {
		return factor;
	}
	public FinanceParams setFactor(double factor) {
		this.factor = factor;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public FinanceParams setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public Boolean getPayment() {
		return payment;
	}
	public FinanceParams setPayment(Boolean payment) {
		this.payment = payment;
		return this;
	}
	public boolean isPending() {
		return pending;
	}
	public FinanceParams setPending(boolean pending) {
		this.pending = pending;
		return this;
	}
	public boolean isBatched() {
		return batched;
	}
	public FinanceParams setBatched(boolean batched) {
		this.batched = batched;
		return this;
	}
	public boolean isReturned() {
		return returned;
	}
	public FinanceParams setReturned(boolean returned) {
		this.returned = returned;
		return this;
	}
	public boolean isPaid() {
		return paid;
	}
	public FinanceParams setPaid(boolean paid) {
		this.paid = paid;
		return this;
	}
	public boolean isSettled() {
		return settled;
	}
	public FinanceParams setSettled(boolean settled) {
		this.settled = settled;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public FinanceParams setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public FinanceParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean hasConfidentialityRole() {
		return hasConfidentialityRole;
	}
	public FinanceParams setHasConfidentialityRole(boolean hasConfidentialityRole) {
		this.hasConfidentialityRole = hasConfidentialityRole;
		return this;
	}
	
	public Integer getPayMethod() {
		return payMethod;
	}
	public FinanceParams setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public int getOrder() {
		return order;
	}
	public FinanceParams setOrder(int order) {
		this.order = order;
		return this;
	}
	public FinanceParams setConfidential(Boolean confidential) {
		if (confidential == null) {
			setSecurityLevel( null );	
		} else {
			setSecurityLevel(confidential?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL);
		}
		return this;
	}
	public boolean isPayroll() {
		return isPayroll;
	}
	public FinanceParams setIsPayroll(boolean isPayroll) {
		this.isPayroll = isPayroll;
		return this;
	}
	
	
}
