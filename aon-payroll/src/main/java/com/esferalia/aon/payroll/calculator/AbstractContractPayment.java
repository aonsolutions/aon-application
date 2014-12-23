package com.esferalia.aon.payroll.calculator;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public abstract class AbstractContractPayment<T extends AbstractContractPayment<T>> implements IContractPayment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
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
	
	
	public AbstractContractPayment() {
	}
	
	
	public AbstractContractPayment(IContractPayment contractPayment){
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
	
	@SuppressWarnings("unchecked")
	public AbstractContractPayment<T> setExpressionScope(ExpressionScope expressionScope) {
		this.expressionScope = expressionScope;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setId(Integer id) {
		this.id = id;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setName(String name) {
		this.name = name;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setType(PaymentType type) {
		this.type = type;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setDescription(String description) {
		this.description = description;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setExpression(String expression) {
		this.expression = expression;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setStartDate(Date startDate) {
		this.startDate = startDate;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setEndDate(Date endDate) {
		this.endDate = endDate;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setMonth(Month month) {
		this.month = month;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setDescriptionDecorable(boolean descriptionDecorable) {
		this.descriptionDecorable = descriptionDecorable;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setAmount(Double amount) {
		this.amount = amount;
		return (T)this;
	}


	@SuppressWarnings("unchecked")
	public T setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
		return (T)this;
	}

	public ExpressionScope getExpressionScope() {
		return expressionScope;
	}
	
	
}