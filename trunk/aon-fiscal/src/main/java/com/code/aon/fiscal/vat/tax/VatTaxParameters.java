package com.code.aon.fiscal.vat.tax;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.enumeration.VatPeriod;

public class VatTaxParameters {

	private Date date;
	private Integer year;
	private Date fromDate;
	private Date toDate;
	private VatPeriod vatPeriod;
	private VatTax	vatTax;
	private SecurityLevel securityLevel;

	public VatTaxParameters() {
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
	}

	public VatTax getVatTax() {
		return vatTax;
	}
	public void setVatTax(VatTax vatTax) {
		this.vatTax = vatTax;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
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

	public VatPeriod getVatPeriod() {
		return vatPeriod;
	}
	public void setVatPeriod(VatPeriod vatPeriod) {
		this.vatPeriod = vatPeriod;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

}
