package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

public class Bonus {

	private String naf;
	private String ccc;
	private Date endDate;
	private Date startDate;
	private String description;
	private String expression;
	private boolean employee;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormula() {
		return expression;
	}

	public void setFormula(String formula) {
		this.expression = formula;
	}

	public String getSsNum() {
		return naf;
	}

	public void setNss(String ssNum) {
		this.naf = ssNum;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	
	public boolean isEnterprise() {
		return !employee;
	}
	
	public boolean isEmployee() {
		return employee;
	}
	
	public void setEmployee() {
		this.employee = true;
	}

	@Override
	public String toString() {
		return "SSBonus -> SS Number : " + getSsNum() + ", CCC : " + getCcc() + ", Description : " + getDescription() + ", Formula : " + getFormula() + ", Start : "
				+ getStartDate() + ", End : " + getEndDate();
	}
}