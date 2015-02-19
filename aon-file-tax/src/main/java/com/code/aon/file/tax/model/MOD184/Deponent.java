package com.code.aon.file.tax.model.MOD184;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.file.tax.FileTaxUtil;

public class Deponent {

	private int year;
	private String document;
	private String name;
	private String contactPhone;
	private String contactPerson;
	private String receipt;
	private String complementary;
	private String replacement;
	private String replacedReceipt;
	private int partnerTotal;
	private String entityType;
	private String mainActivity;
	private String foreignEntityType;
	private String foreignObject;
	private String country;
	private double residentPercent;
	private String taxIS;
	private double netSalesAmount;
	
	private String lrDocument;
	private String lrName;
	
	
	private List<Income> incomes;
	private List<Partner> partners;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = FileTaxUtil.changeInvalidCharacters(document);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = FileTaxUtil.changeInvalidCharacters(name);
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = FileTaxUtil.changeInvalidCharacters(contactPhone);
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = FileTaxUtil.changeInvalidCharacters(contactPerson);
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = FileTaxUtil.changeInvalidCharacters(receipt);
	}

	public String getComplementary() {
		return complementary;
	}

	public void setComplementary(String complementary) {
		this.complementary = FileTaxUtil.changeInvalidCharacters(complementary);
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = FileTaxUtil.changeInvalidCharacters(replacement);
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}

	public void setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = FileTaxUtil.changeInvalidCharacters(replacedReceipt);
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

	public String getTaxIS() {
		return taxIS;
	}

	public void setTaxIS(String taxIS) {
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

	public List<Income> getIncomes() {
		if (incomes == null) {
			incomes = new LinkedList<Income>();
		}
		return incomes;
	}

	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}
	
	public List<Partner> getPartners() {
		if (partners == null) {
			partners = new LinkedList<Partner>();
		}
		return partners;
	}
	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}

}
