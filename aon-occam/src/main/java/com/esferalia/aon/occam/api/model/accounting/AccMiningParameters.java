package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;


public class AccMiningParameters implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int domain;
	private Integer[] domains;
	private int year;
	private int periodId;
	private Date startDate;
	private Date endDate;
	private int accountLevel = 4;
	private SecurityLevel securityLevel;
	
	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}
	
	public Integer[] getDomains() {
		return domains;
	}
	public void setDomains(Integer[] domains) {
		this.domains = domains;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getPeriodId() {
		return periodId;
	}

	public void setPeriodId(int periodId) {
		this.periodId = periodId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(int accountLevel) {
		this.accountLevel = accountLevel;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}
