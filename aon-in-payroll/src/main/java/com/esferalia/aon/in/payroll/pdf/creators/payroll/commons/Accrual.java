package com.esferalia.aon.in.payroll.pdf.creators.payroll.commons;

import java.util.Optional;

public class Accrual {
	
	private Optional<Double> amount;
	private Optional<String> description;
	
	public Accrual(Double amount, String description) {
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

	public void setAmount(Double amount) {
		this.amount = Optional.ofNullable(amount);
	}
	
	
	
	
}
