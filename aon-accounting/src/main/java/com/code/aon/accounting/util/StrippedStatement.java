package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class StrippedStatement implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String documentNumber;
	private String concept;
	private double debit;
	private double credit;
	private double financeAmount;
	private int id;
	private Date entryDate;
	private int entryType;
	
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getFinanceAmount() {
		return financeAmount;
	}
	public void setFinanceAmount(double financeAmount) {
		this.financeAmount = financeAmount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	public int getEntryType() {
		return entryType;
	}
	public void setEntryType(int entryType) {
		this.entryType = entryType;
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
