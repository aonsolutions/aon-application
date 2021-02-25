package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.Optional;

public class DefaultPayrollAccrual {
	
	private Optional<Double> amount;
	private Optional<String> description;
	
	public DefaultPayrollAccrual(Double amount, String description) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.description = Optional.ofNullable(description);
	}

	public Optional<Double> getAmount() {return amount;}
	public Optional<String> getDescription() {return description;}

	@Override
	public String toString() {
		return "{"
				+ "amount: " + amount + ","
				+ "description: " + description 
				+ "}";
	}
	
	
	
	
}
