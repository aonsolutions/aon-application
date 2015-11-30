package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

public class Mod190 implements Serializable {

	private static final long serialVersionUID = -853765073923154482L;
	
	private Integer id;
	private int domain;
	private int enterprise;
	private int year;
	private byte administration;
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
	
	private LinkedList<Mod190Detail> details;

	public Integer getId() { 
		return id;
	}

	public Mod190 setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod190 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod190 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public int getYear() {
		return year;
	}

	public Mod190 setYear(int year) {
		this.year = year;
		return this;
	}

	public byte getAdministration() {
		return administration;
	}

	public Mod190 setAdministration(byte administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public Mod190 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public Mod190 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public String getReceipt() {
		return receipt;
	}

	public Mod190 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public Mod190 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Mod190 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod190 setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod190 setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public Mod190 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod190 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}

	public Mod190 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getReceiptTotal() {
		return receiptTotal;
	}

	public Mod190 setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}

	public Mod190 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public LinkedList<Mod190Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod190Detail>();
		}
		return details;
	}

	public Mod190 setDetails(LinkedList<Mod190Detail> details) {
		this.details = details;
		return this;
	}

}
