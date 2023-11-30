package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

public class ContractParams {

	private String workplace;
	
	private Date from;
	private Date to;
	
	private String tc2;
	
	private String employee;

	public String getWorkplace() {
		return workplace;
	}

	public ContractParams setWorkplace(String workplace) {
		this.workplace = workplace;
		return this;
	}

	public Date getFrom() {
		return from;
	}

	public ContractParams setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Date getTo() {
		return to;
	}

	public ContractParams setTo(Date to) {
		this.to = to;
		return this;
	}

	public String getTc2() {
		return tc2;
	}

	public ContractParams setTc2(String tc2) {
		this.tc2 = tc2;
		return this;
	}

	public String getEmployee() {
		return employee;
	}

	public ContractParams setEmployee(String employee) {
		this.employee = employee;
		return this;
	}
	
}
