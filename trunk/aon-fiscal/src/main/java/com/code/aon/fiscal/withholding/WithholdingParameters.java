package com.code.aon.fiscal.withholding;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.enumeration.Period;

public class WithholdingParameters {

	private Date date;
	private Integer year;
	private Date fromDate;
	private Date toDate;
	private Enterprise enterprise;
	private Period period;
	private SecurityLevel securityLevel;


	public WithholdingParameters() {
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
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

}
