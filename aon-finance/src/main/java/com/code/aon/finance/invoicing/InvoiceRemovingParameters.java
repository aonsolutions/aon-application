package com.code.aon.finance.invoicing;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;

public class InvoiceRemovingParameters {
	
	private Date fromDate;

	private Date toDate;

	private String series;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Integer customerId;
	
	private SecurityLevel securityLevel;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
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
}