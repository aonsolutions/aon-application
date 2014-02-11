package com.code.aon.accounting.util;

import java.util.Date;

import com.code.aon.common.util.CommonUtil;

public class StrippedStatement {
	
	private String documentNumber;
	private String concept;
	private double debit;
	private double credit;
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
	
	public double getDifference() {
		return CommonUtil.round(getDebit() - getCredit());
	}
	
	public boolean isSettled() {
		return (getDifference() == 0);
	}
}
