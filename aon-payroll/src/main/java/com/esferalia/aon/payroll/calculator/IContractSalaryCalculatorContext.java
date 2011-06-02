package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;

public interface IContractSalaryCalculatorContext extends ISalaryCalculatorContext {

	
	public SalaryType getSalaryType();
	
	public String getCcc();
	
	public String getEnterpriseName();
	
	public String getEnterpriseAddress();
	
	public String getEnterpriseDocument();
	
	public SSRegimeType getSSRegime();
	
	
	public String getCategory();

	public String getQuoteGroup();

	public String getEmployeeName();
	
	public String getEmployeeDocument();
	
	public String getSocialSecurityNumber();
	
	public Integer getRegistration();
	
	public Date getSeniorityDate();

	
	public Collection<IContractPayment> getContractPayments() throws AonException;

	public Collection<IContractCost> getContractCosts() throws AonException;

	public Collection<IContractBonus> getContractBonus() throws AonException;

	public Collection<IContractEmbargo> getContractEmbargos() throws AonException;

	public Collection<IContractDeduction> getContractDeductions() throws AonException;
}
