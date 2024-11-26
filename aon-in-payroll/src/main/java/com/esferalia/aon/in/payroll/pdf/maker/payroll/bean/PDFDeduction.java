package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.DeductionType;

public class PDFDeduction {
	
	private Optional<Double> amount;
	private Optional<String> name;
	private Optional<String> description;
	private Optional<Double> base;
	private Optional<Double> percent;	
	private Optional<DeductionType> deductionType = Optional.empty();
	
	public PDFDeduction(Double amount, String name,  String description, Double percent) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.name = Optional.ofNullable(name);
		this.description = Optional.ofNullable(description);
		this.percent = Optional.ofNullable(percent);
		this.deductionType = Optional.empty();
		this.base = Optional.empty();
	}
	
	public PDFDeduction(Double amount, String name, String description, Double percent, DeductionType deductionType) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.name = Optional.ofNullable(name);
		this.description = Optional.ofNullable(description);
		this.percent = Optional.ofNullable(percent);
		this.deductionType= Optional.ofNullable(deductionType);
		this.base = Optional.empty();
	}

	public PDFDeduction(Double amount, String name, String description, Double percent, Double base, DeductionType deductionType) {
		super();
		this.amount = Optional.ofNullable(amount);
		this.name = Optional.ofNullable(name);
		this.description = Optional.ofNullable(description);
		this.percent = Optional.ofNullable(percent);
		this.deductionType= Optional.ofNullable(deductionType);
		this.base = Optional.ofNullable(base);
	}

	public Optional<Double> getAmount() {
		return amount;
	}
	
	public Optional<String> getName() {
	    return name;
	}
	
	public Optional<String> getDescription() {
		return description;
	}
	public Optional<Double> getPercent() {
		return percent;
	}
	
	public Optional<Double> getBase() {
		return base;
	}
	
	public Optional<DeductionType> getDeductionType() {
		return deductionType;
	}

	public PDFDeduction setAmount(Double amount) {
		Optional<Double> opt = Optional.ofNullable(amount);
		this.amount = opt;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((deductionType == null) ? 0 : deductionType.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((percent == null) ? 0 : percent.hashCode());
		return result;
	}
	
}
