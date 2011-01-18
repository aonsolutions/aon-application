package com.code.aon.employee.calculator;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.expression.IExpression;

public interface IContractSalaryCalculatorContext extends ISalaryCalculatorContext {

	public String getCcc();
	
	public String getEnterpriseName();
	
	public String getEnterpriseAddress();
	
	public String getEnterpriseDocument();
	
	
	public String getCategory();

	public String getEmployeeName();
	
	public String getEmployeeDocument();
	
	public String getSocialSecurityNumber();
	
	public Integer getRegistration();
	
	public Date getSeniorityDate();
	
	/**
	public Collection<IExpression> getCrontactExpressions() throws AonException;

	public Collection<IExpression> getApplicationExpressions() throws AonException;

	public Collection<IExpression> getSystemExpressions() throws AonException;
	*/

	public Collection<IContractPayment> getContractPayments() throws AonException;

	public Collection<IContractDeduction> getContractDeductions() throws AonException;

}
