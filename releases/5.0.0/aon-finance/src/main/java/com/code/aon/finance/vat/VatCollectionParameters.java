package com.code.aon.finance.vat;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;

public class VatCollectionParameters {

	private Date fromDate;
	private Date toDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private String fromSeries;
	private String toSeries;
	private Integer fromNumber;
	private Integer toNumber;
	private VatType vatType;
	private VatReportType vatReportType;
	private Double vatPercent;
	private Double surchargePercent;
	private SecurityLevel securityLevel;

	/**
	 * Fecha.
	 */
	private Date date;

	public VatCollectionParameters() {
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setVatType(null);
	}

	public VatType getVatType() {
		return vatType;
	}

	public void setVatType(VatType vatType) {
		this.vatType = vatType;
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

	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	public String getFromSeries() {
		return fromSeries;
	}
	public void setFromSeries(String fromSeries) {
		this.fromSeries = fromSeries;
	}

	public String getToSeries() {
		return toSeries;
	}
	public void setToSeries(String toSeries) {
		this.toSeries = toSeries;
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

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public VatReportType getVatReportType() {
		return vatReportType;
	}
	public void setVatReportType(VatReportType vatReportType) {
		this.vatReportType = vatReportType;
	}

	public Double getVatPercent() {
		return vatPercent;
	}
	public void setVatPercent(Double vatPercent) {
		this.vatPercent = vatPercent;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(Double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}
}
