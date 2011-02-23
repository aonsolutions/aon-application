package com.esferalia.aon.salary.calculator;

import java.util.Date;

import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class SalaryCalculatorContext implements ISalaryCalculatorContext{
	
	private ISalaryProxy salaryProxy;
	private Date issueDate;
	private Date startDate;
	private Date endDate;
	private ExpressionContext ExpressionContext; 

	public SalaryCalculatorContext() {
		
	}

	public SalaryCalculatorContext(Date startDate, Date endDate, Date issueDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.issueDate = issueDate;
	}
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
	public ExpressionContext getExpressionContext() {
		return ExpressionContext;
	}
	public void setExpressionContext(ExpressionContext expressionContext) {
		ExpressionContext = expressionContext;
	}
}
