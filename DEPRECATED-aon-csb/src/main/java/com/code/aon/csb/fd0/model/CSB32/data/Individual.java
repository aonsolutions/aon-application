package com.code.aon.csb.fd0.model.CSB32.data;

import java.util.Date;

import com.code.aon.csb.fd0.core.Account;

public class Individual {

	private String documentNumber;
	
	private Integer provinceNumber;
	
	private String ineCode;
	
	private String paymentPost;
	
	private Double amount;
	
	private Date expiryDate;
	
	private Integer documentType;
	
	private Date paymentDate;
	
	private Integer aceptedCode;
	
	private Integer expenseClause;

	private Account account;
	
	private String efectPayer;
	
	private String efectPayed;
	
	private String aditionalData;
	
	private String payedAddress;
	
	private Integer payedPostPostalCode;
	
	private String payedPost;
	
	private Integer payedPostProvince;
	
	private String payedPostINE;
	
	private String payedDocument;

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * @return the aceptedCode
	 */
	public Integer getAceptedCode() {
		return aceptedCode;
	}

	/**
	 * @param aceptedCode the aceptedCode to set
	 */
	public void setAceptedCode(Integer aceptedCode) {
		this.aceptedCode = aceptedCode;
	}

	/**
	 * @return the aditionalData
	 */
	public String getAditionalData() {
		return aditionalData;
	}

	/**
	 * @param aditionalData the aditionalData to set
	 */
	public void setAditionalData(String aditionalData) {
		this.aditionalData = aditionalData;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the documentNumber
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}

	/**
	 * @param documentNumber the documentNumber to set
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
	 * @return the documentType
	 */
	public Integer getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
	}

	/**
	 * @return the efectPayed
	 */
	public String getEfectPayed() {
		return efectPayed;
	}

	/**
	 * @param efectPayed the efectPayed to set
	 */
	public void setEfectPayed(String efectPayed) {
		this.efectPayed = efectPayed;
	}

	/**
	 * @return the efectPayer
	 */
	public String getEfectPayer() {
		return efectPayer;
	}

	/**
	 * @param efectPayer the efectPayer to set
	 */
	public void setEfectPayer(String efectPayer) {
		this.efectPayer = efectPayer;
	}

	/**
	 * @return the expenseClause
	 */
	public Integer getExpenseClause() {
		return expenseClause;
	}

	/**
	 * @param expenseClause the expenseClause to set
	 */
	public void setExpenseClause(Integer expenseClause) {
		this.expenseClause = expenseClause;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the ineCode
	 */
	public String getIneCode() {
		return ineCode;
	}

	/**
	 * @param ineCode the ineCode to set
	 */
	public void setIneCode(String ineCode) {
		this.ineCode = ineCode;
	}

	/**
	 * @return the payedAddress
	 */
	public String getPayedAddress() {
		return payedAddress;
	}

	/**
	 * @param payedAddress the payedAddress to set
	 */
	public void setPayedAddress(String payedAddress) {
		this.payedAddress = payedAddress;
	}

	/**
	 * @return the payedDocument
	 */
	public String getPayedDocument() {
		return payedDocument;
	}

	/**
	 * @param payedDocument the payedDocument to set
	 */
	public void setPayedDocument(String payedDocument) {
		this.payedDocument = payedDocument;
	}

	/**
	 * @return the payedPost
	 */
	public String getPayedPost() {
		return payedPost;
	}

	/**
	 * @param payedPost the payedPost to set
	 */
	public void setPayedPost(String payedPost) {
		this.payedPost = payedPost;
	}

	/**
	 * @return the payedPostINE
	 */
	public String getPayedPostINE() {
		return payedPostINE;
	}

	/**
	 * @param payedPostINE the payedPostINE to set
	 */
	public void setPayedPostINE(String payedPostINE) {
		this.payedPostINE = payedPostINE;
	}

	/**
	 * @return the payedPostPostalCode
	 */
	public Integer getPayedPostPostalCode() {
		return payedPostPostalCode;
	}

	/**
	 * @param payedPostPostalCode the payedPostPostalCode to set
	 */
	public void setPayedPostPostalCode(Integer payedPostPostalCode) {
		this.payedPostPostalCode = payedPostPostalCode;
	}

	/**
	 * @return the payedPostProvince
	 */
	public Integer getPayedPostProvince() {
		return payedPostProvince;
	}

	/**
	 * @param payedPostProvince the payedPostProvince to set
	 */
	public void setPayedPostProvince(Integer payedPostProvince) {
		this.payedPostProvince = payedPostProvince;
	}

	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the paymentPost
	 */
	public String getPaymentPost() {
		return paymentPost;
	}

	/**
	 * @param paymentPost the paymentPost to set
	 */
	public void setPaymentPost(String paymentPost) {
		this.paymentPost = paymentPost;
	}

	/**
	 * @return the provinceNumber
	 */
	public Integer getProvinceNumber() {
		return provinceNumber;
	}

	/**
	 * @param provinceNumber the provinceNumber to set
	 */
	public void setProvinceNumber(Integer provinceNumber) {
		this.provinceNumber = provinceNumber;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "INDIVIDUAL ";
		description += "DOCUMENT "+documentNumber+"; ";
		description += "PROVINCE "+provinceNumber+"; ";
		description += "INE "+ineCode+"; ";
		description += "PAYMENT POST "+paymentPost+"; ";
		description += "AMOUNT "+amount+"; ";
		description += "EXPIRY DATE "+expiryDate+"; ";
		description += "DOC TYPE "+documentType+"; ";
		description += "PAYMENT DATE "+paymentDate+"; ";
		description += "ACEPTED CODE "+aceptedCode+"; ";
		description += "EXPENSE CLAUSE "+expenseClause+"; ";
		description += "ACCOUNT "+account.getCcc()+"; ";
		description += "EFECT PAYER "+efectPayer+"; ";
		description += "EFECT PAYED "+efectPayed+"; ";
		description += "ADITIONAL DATA "+aditionalData+"; ";
		description += "PAYED ADDRESS "+payedAddress+"; ";
		description += "PAYED POST POSTAL CODE "+payedPostPostalCode+"; ";
		description += "PAYED POST "+payedPost+"; ";
		description += "PAYED POST PROVINCE "+payedPostProvince+"; ";
		description += "PAYED POST INE "+payedPostINE+"; ";
		description += "PAYED DOCUMENT "+payedDocument+"; ";
		return description;
	}
	
}
