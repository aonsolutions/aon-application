package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

public class Mod193 implements Serializable {

	private static final long serialVersionUID = 3763668880785687130L;
	
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
	private double retentionBaseTotal;
	private double retentionTotal;
	private double depositRetentionTotal;
	private double expensesTotal;
	
	private boolean nature;	
	
	private LinkedList<Mod193Detail> details;
	private LinkedList<Mod193Detail> expenses;

	public Integer getId() { 
		return id;
	}

	public Mod193 setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod193 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod193 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public int getYear() {
		return year;
	}

	public Mod193 setYear(int year) {
		this.year = year;
		return this;
	}

	public byte getAdministration() {
		return administration;
	}

	public Mod193 setAdministration(byte administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public Mod193 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public Mod193 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public String getReceipt() {
		return receipt;
	}

	public Mod193 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public Mod193 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Mod193 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod193 setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod193 setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public Mod193 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod193 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}

	public Mod193 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getRetentionBaseTotal() {
		return retentionBaseTotal;
	}

	public Mod193 setRetentionBaseTotal(double retentionBaseTotal) {
		this.retentionBaseTotal = retentionBaseTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}

	public Mod193 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public double getDepositRetentionTotal() {
		return depositRetentionTotal;
	}

	public Mod193 setDepositRetentionTotal(double depositRetentionTotal) {
		this.depositRetentionTotal = depositRetentionTotal;
		return this;
	}

	public double getExpensesTotal() {
		return expensesTotal;
	}

	public Mod193 setExpensesTotal(double expensesTotal) {
		this.expensesTotal = expensesTotal;
		return this;
	}

	public boolean isNature() {
		return nature;
	}

	public Mod193 setNature(boolean nature) {
		this.nature = nature;
		return this;
	}

	public LinkedList<Mod193Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod193Detail>();
		}
		return details;
	}

	public Mod193 setDetails(LinkedList<Mod193Detail> details) {
		this.details = details;
		return this;
	}

	public LinkedList<Mod193Detail> getExpenses() {
		if (expenses == null) {
			expenses = new LinkedList<Mod193Detail>();
		}
		return expenses;
	}

	public Mod193 setExpenses(LinkedList<Mod193Detail> expenses) {
		this.expenses = expenses;
		return this;
	}

}
