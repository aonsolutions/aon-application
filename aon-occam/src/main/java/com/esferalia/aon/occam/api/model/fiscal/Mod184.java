package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Mod184 implements Serializable {

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
	
	
	private ArrayList<Mod184Income> incomes;
	private ArrayList<Mod184Partner> partners;

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

	public int getPartnerTotal() {
		return partnerTotal;
	}

	public void setPartnerTotal(int partnerTotal) {
		this.partnerTotal = partnerTotal;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(String mainActivity) {
		this.mainActivity = mainActivity;
	}

	public String getForeignEntityType() {
		return foreignEntityType;
	}

	public void setForeignEntityType(String foreignEntityType) {
		this.foreignEntityType = foreignEntityType;
	}

	public String getForeignObject() {
		return foreignObject;
	}

	public void setForeignObject(String foreignObject) {
		this.foreignObject = foreignObject;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getResidentPercent() {
		return residentPercent;
	}

	public void setResidentPercent(double residentPercent) {
		this.residentPercent = residentPercent;
	}

	public boolean isTaxIS() {
		return taxIS;
	}

	public void setTaxIS(boolean taxIS) {
		this.taxIS = taxIS;
	}

	public double getNetSalesAmount() {
		return netSalesAmount;
	}

	public void setNetSalesAmount(double netSalesAmount) {
		this.netSalesAmount = netSalesAmount;
	}

	public String getLrDocument() {
		return lrDocument;
	}

	public void setLrDocument(String lrDocument) {
		this.lrDocument = lrDocument;
	}

	public String getLrName() {
		return lrName;
	}

	public void setLrName(String lrName) {
		this.lrName = lrName;
	}

	public ArrayList<Mod184Income> getIncomes() {
		if (incomes == null) {
			incomes = new ArrayList<Mod184Income>();
		}
		return incomes;
	}

	public void setIncomes(ArrayList<Mod184Income> incomes) {
		this.incomes = incomes;
	}

	public ArrayList<Mod184Partner> getPartners() {
		if (partners == null) {
			partners = new ArrayList<Mod184Partner>();
		}
		return partners;
	}

	public void setPartners(ArrayList<Mod184Partner> partners) {
		this.partners = partners;
	}

}
