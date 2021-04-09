package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Optional;

public class PDFDeduction {
	
	private Optional<Double> amount;
	private Optional<String> description;
	private Optional<Double> percent;
	
	public PDFDeduction(Double amount, String description, Double percent) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.description = Optional.ofNullable(description);
		this.percent = Optional.ofNullable(percent);
	}

	public Optional<Double> getAmount() {return amount;}
	public Optional<String> getDescription() {return description;}
	public Optional<Double> getPercent() {return percent;}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((percent == null) ? 0 : percent.hashCode());
		return result;
	}

	public void setAmount(Double amount) {
		
		Optional<Double> opt = Optional.ofNullable(amount);
		
		this.amount = opt;
	}	
	
	
	
}
