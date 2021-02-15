package com.esferalia.aon.in.payroll.pdf.creators.budget.beans;

import java.util.Optional;

public class Budget_item {

	private Optional<String> name;
	private Optional<Double> price;
	
	public Budget_item(String name, Double price) {
		super();
		this.name = Optional.ofNullable(name);
		this.price = Optional.ofNullable(price);
	}

	public Optional<String> getName() {
		return name;
	}

	public Optional<Double> getPrice() {
		return price;
	}
	
	
	
}
