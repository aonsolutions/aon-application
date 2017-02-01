package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class FinanceParams implements Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private int domain;
	private Date from;
	private Date to;
	private boolean confidential; 
	private boolean hasConfidentialityRole;
	private Boolean payment;
	
	private Integer registry;
	private Double amount;
	private boolean nearbyNumbers;
	private double factor = 5;
	private String concept;
	private String referenceCode;
	
	public int getDomain() {
		return domain;
	}
	public FinanceParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Date getFrom() {
		return from;
	}
	public FinanceParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public FinanceParams setTo(Date to) {
		this.to = to;
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
	public String getReferenceCode() {
		return referenceCode;
	}
	public FinanceParams setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public boolean isConfidential() {
		return confidential;
	}
	public FinanceParams setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	public boolean hasConfidentialityRole() {
		return hasConfidentialityRole;
	}
	public FinanceParams setHasConfidentialityRole(boolean hasConfidentialityRole) {
		this.hasConfidentialityRole = hasConfidentialityRole;
		return this;
	}
}
