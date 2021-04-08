package com.esferalia.aon.in.payroll.pdf.template.commons;

import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class Deduction implements IDeduction {

	String name;
	double amount;
	DeductionType type;
	String description;

	@Override
	public DeductionType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getExpression() {
		return null;
	}

	public Deduction setName(String name) {
		this.name = name;
		return this;
	}

	public Deduction setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Deduction setType(DeductionType type) {
		this.type = type;
		return this;
	}

	public Deduction setDescription(String description) {
		this.description = description;
		return this;
	}

}