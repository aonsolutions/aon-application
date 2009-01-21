package com.code.aon.finance.recording;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.InvoiceType;

public class RecordingParameters {

	private String series;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Date fromDate;
	
	private Date toDate;
	
	private InvoiceType invoiceType;
	
	private Integer registryId;
	
	private SecurityLevel securityLevel;

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

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Integer getRegistryId() {
		return registryId;
	}

	public void setRegistryId(Integer registryId) {
		this.registryId = registryId;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}
