package com.esferalia.aon.payroll.calculator;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SimpleContractPayment implements IContractPayment{
	
	private Integer id;
	private String name;
	private Integer conceptId;
	private PaymentType type;
	private String description;
	private String expression;
	private String irpfExpression;
	private String quoteExpression;
	private Date startDate;
	private Date endDate;
	private Month month;
	private boolean readOnly;
	private boolean descriptionDecorable;
	private Double amount;
	private ExpressionScope expressionScope;
	private SalaryType salaryType;
	
	
	public SimpleContractPayment() {
	}
	
	
	public SimpleContractPayment(IContractPayment contractPayment){
		type = contractPayment.getType();
		name = contractPayment.getName();
		month = contractPayment.getMonth();
		startDate = contractPayment.getStartDate();
		endDate = contractPayment.getEndDate();
		conceptId = contractPayment.getConceptId();
		expression = contractPayment.getExpression();
		expressionScope = contractPayment.getScope();
		irpfExpression = contractPayment.getIrpfExpression();
		quoteExpression = contractPayment.getQuoteExpression();
		description = contractPayment.getDescription();
		salaryType = contractPayment.getSalaryType();
		descriptionDecorable = contractPayment.isDescriptionDecorable();
		
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public Integer getConceptId() {
		return conceptId;
	}
	
	@Override
	public PaymentType getType() {
		return type;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ExpressionScope getScope() {
		return expressionScope;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public Month getMonth() {
		return month;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public String getIrpfExpression() {
		return irpfExpression;
	}

	@Override
	public String getQuoteExpression() {
		return quoteExpression;
	}

	@Override
	public SalaryType getSalaryType() {
		return salaryType;
	}
	
	@Override
	public boolean isDescriptionDecorable() {
		return descriptionDecorable;
	}
}