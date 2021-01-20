package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.Optional;

public class DefaultPayrollAccrual {
	
	private Optional<Double> amount;
	private Optional<String> description;
	
	public DefaultPayrollAccrual(Optional<Double> amount, Optional<String> description) {
		super();
		this.amount = amount;
		this.description = description;
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
