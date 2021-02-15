package com.esferalia.aon.in.payroll.pdf.creators.budget.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Budget {

	private Optional<String> budget_number;
	private Optional<Client_data> client;
	private Optional<ArrayList<Budget_item>> products;
	private Optional<Double> tax_percent;
	private Optional<Double> tax_add;
	private Optional<Double> tax_base;
	private Optional<Double> tax_total;
	private Optional<Double> budget_total;
 	
	public Budget(String budget_number, Client_data client, ArrayList<Budget_item> products,Double tax_percent, Double tax_add, Double tax_base, Double tax_total, Double budget_total) {
		super();
		this.budget_number = 	Optional.ofNullable(budget_number);
		this.client = 			Optional.ofNullable(client);
		this.products = 		Optional.ofNullable(products);
		this.tax_percent = 		Optional.ofNullable(tax_percent);
		this.tax_add = 			Optional.ofNullable(tax_add);
		this.tax_base = 		Optional.ofNullable(tax_base);
		this.tax_total = 		Optional.ofNullable(tax_total);
		this.budget_total = 	Optional.ofNullable(budget_total);
	}

	public Optional<String> getBudget_number() {
		return budget_number;
	}

	public Optional<Client_data> getClient() {
		return client;
	}

	public Optional<ArrayList<Budget_item>> getProducts() {
		return products;
	}

	public Optional<Double> getTax_percent() {
		return tax_percent;
	}

	public Optional<Double> getTax_add() {
		return tax_add;
	}

	public Optional<Double> getTax_base() {
		return tax_base;
	}

	public Optional<Double> getTax_total() {
		return tax_total;
	}

	public Optional<Double> getBudget_total() {
		return budget_total;
	}

	@Override
	public String toString() {
		return "Budget [budget_number=" + budget_number + ", client=" + client + ", products=" + products + "]";
	}
	
	
	
		
}
