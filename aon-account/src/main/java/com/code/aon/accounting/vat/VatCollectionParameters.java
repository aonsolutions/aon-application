package com.code.aon.accounting.vat;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.VatType;

public class VatCollectionParameters {

	/**
	 * Desde fecha.
	 */
	private Date fromDate;

	/**
	 * Hasta fecha.
	 */
	private Date toDate;

	private VatType vatType;
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
}
