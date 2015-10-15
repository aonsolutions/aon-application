package com.esferalia.aon.payroll.calculator;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SimpleSystemPayment extends AbstractContractPayment<SimpleSystemPayment> implements ISystemPayment{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	int domain;
	

	public SimpleSystemPayment(IContractPayment contractPayment, int domain) {
		super(contractPayment);
		this.domain = domain;
	}
	
	public int getDomain() {
		return domain;
	}
	
	// ----------------------------------------------------------- Unmodifiable
	
	@Override
	public SimpleSystemPayment setAmount(Double amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractContractPayment<SimpleSystemPayment> setExpressionScope(
			ExpressionScope expressionScope) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setId(Integer id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setConceptId(Integer conceptId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setType(PaymentType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setDescription(String description) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setExpression(String expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setIrpfExpression(String irpfExpression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setQuoteExpression(String quoteExpression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setStartDate(Date startDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setEndDate(Date endDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setMonth(Month month) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setDescriptionDecorable(
			boolean descriptionDecorable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleSystemPayment setSalaryType(SalaryType salaryType) {
		throw new UnsupportedOperationException();
	}
	
	
	
}
