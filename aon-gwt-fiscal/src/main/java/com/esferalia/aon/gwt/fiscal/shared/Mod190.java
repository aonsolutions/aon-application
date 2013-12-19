package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class Mod190 implements Serializable, IsSerializable {

	public static final ProvidesKey<Mod190> PROVIDES_KEY = new ProvidesKey<Mod190>() {
		@Override
		public Object getKey(Mod190 mod190) {
			return mod190 == null ? null : mod190.getId();
		}
	};

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

	// private boolean status;
	// private boolean complementary;
	// private int number;
	// private int replaced_number;

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

	public int getAdministration() {
		return administration;
	}

	public void setAdministration(int administration) {
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

	public double getReceiptTotal() {
		return receiptTotal;
	}

	public void setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}

	public void setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
	}

	// ------------------------------------------------------------ Ops. Methods

}
