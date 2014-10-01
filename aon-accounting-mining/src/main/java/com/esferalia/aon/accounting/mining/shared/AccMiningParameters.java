package com.esferalia.aon.accounting.mining.shared;

import java.io.Serializable;
import java.util.Date;

public class AccMiningParameters implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int domain;
	private int year;
	private int periodId;
	private Date startDate;
	private Date endDate;
	
	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
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

}
