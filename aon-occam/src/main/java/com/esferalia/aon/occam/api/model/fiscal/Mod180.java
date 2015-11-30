package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

public class Mod180 implements Serializable {

	private static final long serialVersionUID = -1799677687517304432L;
	
	private Integer id;
	private int domain;
	private int enterprise;
	private int year;
	private int administration;
	private boolean confidential;
	private boolean replacement;
	private String receipt;
	private String replacedReceipt;
	private String comments;
	private String document;
	private String name;
	private String contactPerson;
	private String contactPhone;
	private int receiverCountTotal;
	private double receiptTotal;
	private double retentionTotal;
	
	private LinkedList<Mod180Detail> details;

	public Integer getId() { 
		return id;
	}

	public Mod180 setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod180 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod180 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public int getYear() {
		return year;
	}

	public Mod180 setYear(int year) {
		this.year = year;
		return this;
	}

	public int getAdministration() {
		return administration;
	}

	public Mod180 setAdministration(int administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public Mod180 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public Mod180 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public String getReceipt() {
		return receipt;
	}

	public Mod180 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public Mod180 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Mod180 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod180 setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod180 setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public Mod180 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod180 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}

	public Mod180 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getReceiptTotal() {
		return receiptTotal;
	}

	public Mod180 setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}

	public Mod180 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public LinkedList<Mod180Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod180Detail>();
		}
		return details;
	}

	public Mod180 setDetails(LinkedList<Mod180Detail> details) {
		this.details = details;
		return this;
	}
	
}
