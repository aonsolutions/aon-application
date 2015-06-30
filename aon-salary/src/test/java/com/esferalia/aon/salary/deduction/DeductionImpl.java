package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class DeductionImpl implements IDeduction {

	private String name;
	private Double amount;
	private String expression;
	private String description;
	private DeductionType type;
	
	@Override
	public DeductionType getType() {
		return type;
	}
	
	public DeductionImpl setType(DeductionType type) {
		this.type = type;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public DeductionImpl setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public double getAmount() {
		return amount;
	}
	
	public DeductionImpl setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public DeductionImpl setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String getExpression() {
		return expression;
	}
	
	public DeductionImpl setExpression(String expression) {
		this.expression = expression;
		return this;
	}

}
