package com.esferalia.aon.salary.calculator;

import java.util.Date;

import com.esferalia.aon.salary.ISalaryProxy;

public class SalaryCalculatorContext {
	
	private ISalaryProxy salaryProxy;
	private Date issueDate;
	private Date startDate;
	private Date endDate;

	public ISalaryProxy getSalaryProxy() {
		return salaryProxy;
	}
	public void setSalaryProxy(ISalaryProxy salaryProxy) {
		this.salaryProxy = salaryProxy;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
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
