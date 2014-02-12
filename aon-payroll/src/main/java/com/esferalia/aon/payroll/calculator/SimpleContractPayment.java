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
		id = contractPayment.getId();
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
	
	public void setExpressionScope(ExpressionScope expressionScope) {
		this.expressionScope = expressionScope;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}


	public void setType(PaymentType type) {
		this.type = type;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public void setExpression(String expression) {
		this.expression = expression;
	}


	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}


	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public void setMonth(Month month) {
		this.month = month;
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


	public void setDescriptionDecorable(boolean descriptionDecorable) {
		this.descriptionDecorable = descriptionDecorable;
	}


	public void setAmount(Double amount) {
		this.amount = amount;
	}


	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}

	public ExpressionScope getExpressionScope() {
		return expressionScope;
	}

	
}