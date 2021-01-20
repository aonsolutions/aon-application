package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.Optional;

public class DefaultPayrollDeduction {
	
	private Optional<Double> amount;
	private Optional<String> description;
	private Optional<Double> percent;
	
	public DefaultPayrollDeduction(Optional<Double> amount, Optional<String> description, Optional<Double> percent) {
		super();
		this.amount = amount;
		this.description = description;
		this.percent = percent;
	}

	public Optional<Double> getAmount() {return amount;}
	public Optional<String> getDescription() {return description;}
	public Optional<Double> getPercent() {return percent;}	
	
}
