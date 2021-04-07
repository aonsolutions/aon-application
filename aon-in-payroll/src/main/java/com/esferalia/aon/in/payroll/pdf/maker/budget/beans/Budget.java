package com.esferalia.aon.in.payroll.pdf.maker.budget.beans;

import java.util.ArrayList;
import java.util.Optional;

public class Budget {

	private Optional<String> budget_number;
	private Optional<Client_data> client;
	private Optional<ArrayList<Budget_item>> products;
	private Optional<ArrayList<Term>> terms;
	private Optional<Double> tax_percent;
	private Optional<Double> tax_add;
	private Optional<Double> tax_base;
	private Optional<Double> tax_total;
	private Optional<Double> budget_total;
 	
	public Budget(String budget_number, Client_data client, ArrayList<Budget_item> products, ArrayList<Term> terms, Double tax_percent, Double tax_add, Double tax_base, Double tax_total, Double budget_total) {
		super();
		this.budget_number = 	Optional.ofNullable(budget_number);
		this.client = 			Optional.ofNullable(client);
		this.products = 		Optional.ofNullable(products);
		this.terms = 			Optional.ofNullable(terms);
		this.tax_percent = 		Optional.ofNullable(tax_percent);
		this.tax_add = 			Optional.ofNullable(tax_add);
		this.tax_base = 		Optional.ofNullable(tax_base);
		this.tax_total = 		Optional.ofNullable(tax_total);
		this.budget_total = 	Optional.ofNullable(budget_total);
	}

	public Optional<String> getBudget_number() {
		return budget_number;
	}

	public void setBudget_number(Optional<String> budget_number) {
		this.budget_number = budget_number;
	}

	public Optional<Client_data> getClient() {
		return client;
	}

	public void setClient(Optional<Client_data> client) {
		this.client = client;
	}

	public Optional<ArrayList<Budget_item>> getProducts() {
		return products;
	}

	public void setProducts(Optional<ArrayList<Budget_item>> products) {
		this.products = products;
	}

	public Optional<ArrayList<Term>> getTerms() {
		return terms;
	}

	public void setTerms(Optional<ArrayList<Term>> terms) {
		this.terms = terms;
	}

	public Optional<Double> getTax_percent() {
		return tax_percent;
	}

	public void setTax_percent(Optional<Double> tax_percent) {
		this.tax_percent = tax_percent;
	}

	public Optional<Double> getTax_add() {
		return tax_add;
	}

	public void setTax_add(Optional<Double> tax_add) {
		this.tax_add = tax_add;
	}

	public Optional<Double> getTax_base() {
		return tax_base;
	}

	public void setTax_base(Optional<Double> tax_base) {
		this.tax_base = tax_base;
	}

	public Optional<Double> getTax_total() {
		return tax_total;
	}

	public void setTax_total(Optional<Double> tax_total) {
		this.tax_total = tax_total;
	}

	public Optional<Double> getBudget_total() {
		return budget_total;
	}

	public void setBudget_total(Optional<Double> budget_total) {
		this.budget_total = budget_total;
	}

	@Override
	public String toString() {
		return "Budget :\t\n{ \n\tbudget_number: \t\t" + budget_number + ", \n\tclient: \t\t" + client
				+ ", \n\tproducts: \t\t" + products + ", \n\tterms: \t\t" + terms + ", \n\ttax_percent: \t\t"
				+ tax_percent + ", \n\ttax_add: \t\t" + tax_add + ", \n\ttax_base: \t\t" + tax_base
				+ ", \n\ttax_total: \t\t" + tax_total + ", \n\tbudget_total: \t\t" + budget_total + "\n}";
	}



	
	
	
		
}
