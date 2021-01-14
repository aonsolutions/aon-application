package com.esferalia.aon.in.payroll.pdf.creators.payroll.complete.beans;

import java.util.Optional;

public class DetailedPayrollAccrual {
	
	private Optional<Double> amount;
	private Optional<String> description;
	
	public DetailedPayrollAccrual(Optional<Double> amount, Optional<String> description) {
		super();
		this.amount = amount;
		this.description = description;
	}

	public Optional<Double> getAmount() {return amount;}
	public Optional<String> getDescription() {return description;}
	
}
