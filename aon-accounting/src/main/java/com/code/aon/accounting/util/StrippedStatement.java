package com.code.aon.accounting.util;

import java.io.Serializable;
import java.math.BigDecimal;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class StrippedStatement implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String documentNumber;
	private double debit;
	private double credit;
	private boolean emptyDocument;
	
	public String getDocumentNumber() {
		return documentNumber;
	}
	public StrippedStatement setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	public double getDebit() {
		return debit;
	}
	public StrippedStatement setDebit(double debit) {
		this.debit = debit;
		return this;
	}
	public StrippedStatement setDebit(BigDecimal debit) {
		return setDebit(debit==null?0.0:debit.doubleValue());
	}
	public double getCredit() {
		return credit;
	}
	public StrippedStatement setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	public StrippedStatement setCredit(BigDecimal credit) {
		return setCredit(credit==null?0.0:credit.doubleValue());
	}
	public boolean isEmptyDocument() {
		return emptyDocument;
	}
	public StrippedStatement setEmptyDocument(boolean emptyDocument) {
		this.emptyDocument = emptyDocument;
		return this;
	}
	public double getDebitBalance() {
		double d = CommonUtil.round(getDebit() - getCredit()); 
		return d >0?d:0;
	}
	public double getCreditBalance() {
		double d = CommonUtil.round(getCredit() - getDebit()); 
		return d >0?d:0;
	}
	
	public boolean isSettled() {
		return (getDebitBalance() == 0 && getCreditBalance() == 0);
	}
}
