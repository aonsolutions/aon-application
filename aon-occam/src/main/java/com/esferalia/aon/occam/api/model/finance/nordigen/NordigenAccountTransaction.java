package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

public class NordigenAccountTransaction implements Serializable {
	
	private static final long serialVersionUID = -3879480833728917316L;
	
	
	private String transactionId;
	private String entryReference;
	private String checkId;
	private Date bookingDate;
	private Date valueDate;
	private NordigenAccountAmount transactionAmount;
	private String remittanceInformationUnstructured;
	private String purposeCode;
	private String bankTransactionCode;
	private String proprietaryBankTransactionCode;
	private String endToEndId;
	
	public String getTransactionId() {
		return transactionId;
	}
	public NordigenAccountTransaction setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}	
	public String getEntryReference() {
		return entryReference;
	}
	public NordigenAccountTransaction setEntryReference(String entryReference) {
		this.entryReference = entryReference;
		return this;
	}
	public String getCheckId() {
		return checkId;
	}
	public NordigenAccountTransaction setCheckId(String checkId) {
		this.checkId = checkId;
		return this;
	}
	public Date getBookingDate() {
		return bookingDate;
	}
	public NordigenAccountTransaction setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
		return this;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public NordigenAccountTransaction setValueDate(Date valueDate) {
		this.valueDate = valueDate;
		return this;
	}
	public NordigenAccountAmount getTransactionAmount() {
		return transactionAmount;
	}
	public NordigenAccountTransaction setTransactionAmount(NordigenAccountAmount transactionAmount) {
		this.transactionAmount = transactionAmount;
		return this;
	}
	public String getRemittanceInformationUnstructured() {
		return remittanceInformationUnstructured;
	}
	public NordigenAccountTransaction setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
		this.remittanceInformationUnstructured = remittanceInformationUnstructured;
		return this;
	}
	public String getPurposeCode() {
		return purposeCode;
	}
	public NordigenAccountTransaction setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
		return this;
	}
	public String getBankTransactionCode() {
		return bankTransactionCode;
	}
	public NordigenAccountTransaction setBankTransactionCode(String bankTransactionCode) {
		this.bankTransactionCode = bankTransactionCode;
		return this;
	}
	public String getProprietaryBankTransactionCode() {
		return proprietaryBankTransactionCode;
	}
	public NordigenAccountTransaction setProprietaryBankTransactionCode(String proprietaryBankTransactionCode) {
		this.proprietaryBankTransactionCode = proprietaryBankTransactionCode;
		return this;
	}
	public String getEndToEndId() {
		return endToEndId;
	}
	public NordigenAccountTransaction setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
		return this;
	}
	
	
}
