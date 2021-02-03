package com.esferalia.aon.in.payroll.pdf.templates.common_classes;

import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;

public class Payment implements IPayment {

	String name;
	double amount;
	PaymentType type;
	String description;

	@Override
	public PaymentType getType() {
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

	public Payment setName(String name) {
		this.name = name;
		return this;
	}

	public Payment setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Payment setType(PaymentType type) {
		this.type = type;
		return this;
	}

	public Payment setDescription(String description) {
		this.description = description;
		return this;
	}

}
