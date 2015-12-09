package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

public class Mod184 implements Serializable {

	private static final long serialVersionUID = 7849527072278585773L;

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

	private int partnerTotal;
	private String entityType;
	private String mainActivity;
	private String foreignEntityType;
	private String foreignObject;
	private String country;
	private double residentPercent;
	private boolean taxIS;
	private double netSalesAmount;

	private String lrDocument;
	private String lrName;

	private LinkedList<Mod184Income> incomes;
	private LinkedList<Mod184Partner> partners;

	public Integer getId() {
		return id;
	}

	public Mod184 setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod184 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod184 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public int getYear() {
		return year;
	}

	public Mod184 setYear(int year) {
		this.year = year;
		return this;
	}

	public byte getAdministration() {
		return administration;
	}

	public Mod184 setAdministration(byte administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public Mod184 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public Mod184 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}

	public Mod184 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public Mod184 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Mod184 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod184 setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod184 setName(String name) {
		this.name = name;
		return this;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public Mod184 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod184 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public int getPartnerTotal() {
		return partnerTotal;
	}

	public Mod184 setPartnerTotal(int partnerTotal) {
		this.partnerTotal = partnerTotal;
		return this;
	}

	public String getEntityType() {
		return entityType;
	}

	public Mod184 setEntityType(String entityType) {
		this.entityType = entityType;
		return this;
	}

	public String getMainActivity() {
		return mainActivity;
	}

	public Mod184 setMainActivity(String mainActivity) {
		this.mainActivity = mainActivity;
		return this;
	}

	public String getForeignEntityType() {
		return foreignEntityType;
	}

	public Mod184 setForeignEntityType(String foreignEntityType) {
		this.foreignEntityType = foreignEntityType;
		return this;
	}

	public String getForeignObject() {
		return foreignObject;
	}

	public Mod184 setForeignObject(String foreignObject) {
		this.foreignObject = foreignObject;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Mod184 setCountry(String country) {
		this.country = country;
		return this;
	}

	public double getResidentPercent() {
		return residentPercent;
	}

	public Mod184 setResidentPercent(double residentPercent) {
		this.residentPercent = residentPercent;
		return this;
	}

	public boolean isTaxIS() {
		return taxIS;
	}

	public Mod184 setTaxIS(boolean taxIS) {
		this.taxIS = taxIS;
		return this;
	}

	public double getNetSalesAmount() {
		return netSalesAmount;
	}

	public Mod184 setNetSalesAmount(double netSalesAmount) {
		this.netSalesAmount = netSalesAmount;
		return this;
	}

	public String getLrDocument() {
		return lrDocument;
	}

	public Mod184 setLrDocument(String lrDocument) {
		this.lrDocument = lrDocument;
		return this;
	}

	public String getLrName() {
		return lrName;
	}

	public Mod184 setLrName(String lrName) {
		this.lrName = lrName;
		return this;
	}

	public LinkedList<Mod184Income> getIncomes() {
		if (incomes == null) {
			incomes = new LinkedList<Mod184Income>();
		}
		return incomes;
	}

	public Mod184 setIncomes(LinkedList<Mod184Income> incomes) {
		this.incomes = incomes;
		return this;
	}

	public LinkedList<Mod184Partner> getPartners() {
		if (partners == null) {
			partners = new LinkedList<Mod184Partner>();
		}
		return partners;
	}

	public Mod184 setPartners(LinkedList<Mod184Partner> partners) {
		this.partners = partners;
		return this;
	}

}
