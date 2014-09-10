package com.esferalia.aon.payroll.calculator.junit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.calculator.AbstractContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class MockContractSalaryCalculatorContext extends
		AbstractContractSalaryCalculatorContext {

	private Date startDate;
	private Date endDate;
	private Date seniorityDate;

	private SalaryType salaryType = SalaryType.SALARY;
	private SSRegimeType ssRegime = SSRegimeType.GENERAL;

	private Collection<IContractCost> costs = new ArrayList<IContractCost>();
	private Collection<IContractBonus> bonuses = new ArrayList<IContractBonus>();
	private Collection<IContractPayment> payments = new ArrayList<IContractPayment>();
	private Collection<IContractEmbargo> embargos = new ArrayList<IContractEmbargo>();
	private Collection<IContractDeduction> deductions = new ArrayList<IContractDeduction>();

	private ExpressionContext expressionContext = new ExpressionContext();

	public MockContractSalaryCalculatorContext() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		this.startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		this.endDate = calendar.getTime();
	}

	@Override
	public SalaryType getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
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
	public SSRegimeType getSSRegime() {
		return ssRegime;
	}

	public void setSSRegime(SSRegimeType ssRegime) {
		this.ssRegime = ssRegime;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuoteGroup() {
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
		return seniorityDate;
	}

	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	@Override
	public ExpressionContext getSystemExpressionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionContext getImplicitExpressionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionContext getAgreementExpressionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IContractPayment> getAgreementPayments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return payments;
	}
	
	public void addContractPayment(IContractPayment payment){
		payments.add(payment);
	}

	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return costs;
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		return bonuses;
	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return embargos;
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return deductions;
	}

	@Override
	public Date getIrpfDate() {
		return getEndDate();
	}

	@Override
	public Date getChargeDate() {
		return getEndDate();
	}

	@Override
	public Date getIssueDate() {
		return getEndDate();
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return expressionContext;
	}

	public void setExpressionContext(ExpressionContext expressionContext) {
		this.expressionContext = expressionContext;
	}

	@Override
	public ISalary getSalary(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}
}
