package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Mod193 implements Serializable {

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
	
	private ArrayList<Mod193Detail> details;
	private ArrayList<Mod193Detail> expenses;

	public Integer getId() { 
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public byte getAdministration() {
		return administration;
	}

	public void setAdministration(byte administration) {
		this.administration = administration;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	
	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public void setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}

	public void setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
	}

	public double getRetentionBaseTotal() {
		return retentionBaseTotal;
	}

	public void setRetentionBaseTotal(double retentionBaseTotal) {
		this.retentionBaseTotal = retentionBaseTotal;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}

	public void setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
	}

	public double getDepositRetentionTotal() {
		return depositRetentionTotal;
	}

	public void setDepositRetentionTotal(double depositRetentionTotal) {
		this.depositRetentionTotal = depositRetentionTotal;
	}

	public double getExpensesTotal() {
		return expensesTotal;
	}

	public void setExpensesTotal(double expensesTotal) {
		this.expensesTotal = expensesTotal;
	}

	public boolean isNature() {
		return nature;
	}

	public void setNature(boolean nature) {
		this.nature = nature;
	}

	public ArrayList<Mod193Detail> getDetails() {
		if (details == null) {
			details = new ArrayList<Mod193Detail>();
		}
		return details;
	}

	public void setDetails(ArrayList<Mod193Detail> details) {
		this.details = details;
	}

	public ArrayList<Mod193Detail> getExpenses() {
		if (expenses == null) {
			expenses = new ArrayList<Mod193Detail>();
		}
		return expenses;
	}

	public void setExpenses(ArrayList<Mod193Detail> expenses) {
		this.expenses = expenses;
	}

}
