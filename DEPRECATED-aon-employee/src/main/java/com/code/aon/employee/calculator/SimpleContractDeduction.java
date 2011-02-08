package com.code.aon.employee.calculator;

import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SimpleContractDeduction implements IContractDeduction {

	
	private double amount;
	private DeductionType type;
	private String description;
	
	private String name;
	private boolean readOnly;
	private String expression;
	private ExpressionScope scope;
	
	
	@Override
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	@Override
	public DeductionType getType() {
		return type;
	}
	public void setType(DeductionType type) {
		this.type = type;
	}
	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	@Override
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	@Override
	public ExpressionScope getScope() {
		return scope;
	}
	public void setScope(ExpressionScope scope) {
		this.scope = scope;
	}
	
}
