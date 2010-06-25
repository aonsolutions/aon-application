package com.code.aon.fiscal.model303;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.enumeration.VatPeriod;

public class Model303Parameters {

	private Date date;
	private Integer year;
	private Date fromDate;
	private Date toDate;
	private VatPeriod vatPeriod;
	private SecurityLevel securityLevel;

	public Model303Parameters() {
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
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
