package com.esferalia.aon.payroll.calculator.sql;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class SQLContractExtraCalculatorContext implements
		IContractSalaryCalculatorContext {
	
	private IContractSalaryCalculatorContext salaryCalculatorContext;
	
	@Override
	public Date getIssueDate() {
		return salaryCalculatorContext.getIssueDate();
	}

	@Override
	public Date getStartDate() {
		return salaryCalculatorContext.getStartDate();
	}

	@Override
	public Date getEndDate() {
		return salaryCalculatorContext.getEndDate();
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		return salaryCalculatorContext.getSalaryProxy();
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return salaryCalculatorContext.getExpressionContext();
	}

	@Override
	public SalaryType getSalaryType() {
		return salaryCalculatorContext.getSalaryType();
	}

	@Override
	public String getCcc() {
		return salaryCalculatorContext.getCcc();
	}

	@Override
	public String getEnterpriseName() {
		return salaryCalculatorContext.getEnterpriseName();
	}

	@Override
	public String getEnterpriseAddress() {
		return salaryCalculatorContext.getEnterpriseAddress();
	}

	@Override
	public String getEnterpriseDocument() {
		return salaryCalculatorContext.getEnterpriseDocument();
	}

	@Override
	public SSRegimeType getSSRegime() {
		return salaryCalculatorContext.getSSRegime();
	}

	@Override
	public String getCategory() {
		return salaryCalculatorContext.getCategory();
	}

	@Override
	public String getQuoteGroup() {
		return salaryCalculatorContext.getQuoteGroup();
	}

	@Override
	public String getEmployeeName() {
		return salaryCalculatorContext.getEmployeeName();
	}

	@Override
	public String getEmployeeDocument() {
		return salaryCalculatorContext.getEmployeeDocument();
	}

	@Override
	public String getSocialSecurityNumber() {
		return salaryCalculatorContext.getSocialSecurityNumber();
	}

	@Override
	public Integer getRegistration() {
		return salaryCalculatorContext.getRegistration();
	}

	@Override
	public Date getSeniorityDate() {
		return salaryCalculatorContext.getSeniorityDate();
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return salaryCalculatorContext.getContractPayments();
	}

	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return salaryCalculatorContext.getContractCosts();
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		return salaryCalculatorContext.getContractBonus();
	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return salaryCalculatorContext.getContractEmbargos();
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return salaryCalculatorContext.getContractDeductions();
	}

}
