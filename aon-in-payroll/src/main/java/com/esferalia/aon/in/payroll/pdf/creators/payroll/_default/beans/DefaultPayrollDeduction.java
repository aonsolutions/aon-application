package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.Optional;

public class DefaultPayrollDeduction {
	
	private Optional<Double> amount;
	private Optional<String> description;
	private Optional<Double> percent;
	
	public DefaultPayrollDeduction(Double amount, String description, Double percent) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.description = Optional.ofNullable(description);
		this.percent = Optional.ofNullable(percent);
	}

	public Optional<Double> getAmount() {return amount;}
	public Optional<String> getDescription() {return description;}
	public Optional<Double> getPercent() {return percent;}	
	
}
