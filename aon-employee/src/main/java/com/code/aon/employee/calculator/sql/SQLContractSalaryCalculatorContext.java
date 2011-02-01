package com.code.aon.employee.calculator.sql;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.code.aon.employee.calculator.IContractDeduction;
import com.code.aon.employee.calculator.IContractPayment;
import com.code.aon.employee.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class SQLContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext {

	@Override
	public Date getIssueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionContext getExpressionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCcc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSocialSecurityNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRegistration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSeniorityDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
