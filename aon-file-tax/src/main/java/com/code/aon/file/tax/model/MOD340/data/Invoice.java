package com.code.aon.file.tax.model.MOD340.data;

import java.util.Date;


public class Invoice {

	private Integer year;
	private String period;
	private String code;
	private String document;
	private String name;
	private String country;
	private String countryKey;
	private String countryCode;
	private String countryNif;
	private String operation;
	private String issueDate;
	private String operationDate;
	private double percent;
	private double taxableBase;
	private double quota;
	private double total;
	private double costTaxableBase;
	private String invoiceNumber;
	private String documentNumber;

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryKey() {
		return countryKey;
	}
	public void setCountryKey(String countryKey) {
		this.countryKey = countryKey;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryNif() {
		return countryNif;
	}
	public void setCountryNif(String countryNif) {
		this.countryNif = countryNif;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(String operationDate) {
		this.operationDate = operationDate;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getCostTaxableBase() {
		return costTaxableBase;
	}
	public void setCostTaxableBase(double costTaxableBase) {
		this.costTaxableBase = costTaxableBase;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	
}
