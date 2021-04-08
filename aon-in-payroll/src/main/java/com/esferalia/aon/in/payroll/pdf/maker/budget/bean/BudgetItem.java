package com.esferalia.aon.in.payroll.pdf.maker.budget.bean;

import java.util.Optional;

public class BudgetItem {

	private Optional<String> name;
	private Optional<Double> price;
	
	public BudgetItem(String name, Double price) {
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

	@Override
	public String toString() {
		return "Budget_item :\t\n{ \n\tname: \t\t" + name + ", \n\tprice: \t\t" + price + "\n}";
	}
	
	
	
}
