package com.esferalia.aon.gwt.employee.shared;

import java.io.Serializable;
import java.util.Date;

public class Salary implements Serializable {
	
	
	public enum Type {
		SALARY,
		EXTRA,
		SETTLE,
		DELAY,
		NOT_ENJOYED_VACATIONS;
	}

	private int id ;
	
	private Type type;
	
	private Date startDate;
	private Date endDate;
	
	private Date issueDate;
	private Date chargeDate;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
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

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}
	
	
	
	
	
}
