package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class DelegateContractSalaryCalculatorContext<T extends IContractSalaryCalculatorContext> implements
		IContractSalaryCalculatorContext {
	
	protected T ctx;
	
	
	public T getCtx() {
		return ctx;
	}
	
	public DelegateContractSalaryCalculatorContext(T ctx ) {
		this.ctx = ctx;
	}

	public Date getIrpfDate() {
		return ctx.getIrpfDate();
	}

	public Date getChargeDate() {
		return ctx.getChargeDate();
	}

	public Date getIssueDate() {
		return ctx.getIssueDate();
	}

	public Date getStartDate() {
		return ctx.getStartDate();
	}

	public Date getEndDate() {
		return ctx.getEndDate();
	}

	public ISalaryProxy getSalaryProxy() {
		return ctx.getSalaryProxy();
	}

	public SalaryType getSalaryType() {
		return ctx.getSalaryType();
	}

	public ExpressionContext getExpressionContext() {
		return ctx.getExpressionContext();
	}

	public String getCcc() {
		return ctx.getCcc();
	}

	public String getEnterpriseName() {
		return ctx.getEnterpriseName();
	}

	public String getEnterpriseAddress() {
		return ctx.getEnterpriseAddress();
	}

	public String getEnterpriseDocument() {
		return ctx.getEnterpriseDocument();
	}

	public SSRegimeType getSSRegime() {
		return ctx.getSSRegime();
	}

	public String getCategory() {
		return ctx.getCategory();
	}

	public String getQuoteGroup() {
		return ctx.getQuoteGroup();
	}

	public String getEmployeeName() {
		return ctx.getEmployeeName();
	}

	public String getEmployeeDocument() {
		return ctx.getEmployeeDocument();
	}

	public String getSocialSecurityNumber() {
		return ctx.getSocialSecurityNumber();
	}

	public Integer getRegistration() {
		return ctx.getRegistration();
	}

	public Date getSeniorityDate() {
		return ctx.getSeniorityDate();
	}
	
	@Override
	public Collection<IContractPayment> getAgreementPayments() {
		return ctx.getAgreementPayments();
	}
	
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return ctx.getContractPayments();
	}

	public Collection<IContractCost> getContractCosts() throws AonException {
		return ctx.getContractCosts();
	}

	public Collection<IContractBonus> getContractBonus() throws AonException {
		return ctx.getContractBonus();
	}

	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return ctx.getContractEmbargos();
	}

	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return ctx.getContractDeductions();
	}

	public ExpressionContext getSystemExpressionContext() {
		return ctx.getSystemExpressionContext();
	}

	public ExpressionContext getImplicitExpressionContext() {
		return ctx.getImplicitExpressionContext();
	}

	public ExpressionContext getAgreementExpressionContext() {
		return ctx.getAgreementExpressionContext();
	}
	
	@Override
	public IListener getListener() {
		return ctx.getListener();
	}
	
	@Override
	public void setListener(IListener listener) {
		ctx.setListener(listener);
	}
}
