package com.code.aon.finance.invoicing;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;

public class InvoicingParameters {
	
	private Month month;
	
	private int year;
	
	private String series;
	
	private int number;
	
	private Date invoiceDate;
	
	private Integer customerId;
	
	private SecurityLevel securityLevel;
	
	private Integer workPlaceId;
	
	
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Integer getWorkPlaceId() {
		return workPlaceId;
	}

	public void setWorkPlaceId(Integer workPlaceId) {
		this.workPlaceId = workPlaceId;
	}
}