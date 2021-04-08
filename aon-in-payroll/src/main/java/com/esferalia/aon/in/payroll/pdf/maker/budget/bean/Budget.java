package com.esferalia.aon.in.payroll.pdf.maker.budget.bean;

import java.util.ArrayList;
import java.util.Optional;

public class Budget {

	private Optional<String>				budgetNumber;
	private Optional<ClientData>			client;
	private Optional<ArrayList<BudgetItem>>	products;
	private Optional<ArrayList<Term>>		terms;
	private Optional<Double>				taxPercent;
	private Optional<Double>				taxAdd;
	private Optional<Double>				taxBase;
	private Optional<Double>				taxTotal;
	private Optional<Double>				budgetTotal;

	public Budget(
			String budgetNumber, ClientData client, ArrayList<BudgetItem> products, ArrayList<Term> terms,
			Double taxPercent, Double taxAdd, Double taxBase, Double taxTotal, Double budgetTotal
	) {
		super();
		this.budgetNumber = Optional.ofNullable(budgetNumber);
		this.client		  = Optional.ofNullable(client);
		this.products	  = Optional.ofNullable(products);
		this.terms		  = Optional.ofNullable(terms);
		this.taxPercent	  = Optional.ofNullable(taxPercent);
		this.taxAdd		  = Optional.ofNullable(taxAdd);
		this.taxBase	  = Optional.ofNullable(taxBase);
		this.taxTotal	  = Optional.ofNullable(taxTotal);
		this.budgetTotal  = Optional.ofNullable(budgetTotal);
	}

	public Optional<String> getBudgetNumber() {
		return budgetNumber;
	}

	public void setBudgetNumber(Optional<String> budgetNumber) {
		this.budgetNumber = budgetNumber;
	}

	public Optional<ClientData> getClient() {
		return client;
	}

	public void setClient(Optional<ClientData> client) {
		this.client = client;
	}

	public Optional<ArrayList<BudgetItem>> getProducts() {
		return products;
	}

	public void setProducts(Optional<ArrayList<BudgetItem>> products) {
		this.products = products;
	}

	public Optional<ArrayList<Term>> getTerms() {
		return terms;
	}

	public void setTerms(Optional<ArrayList<Term>> terms) {
		this.terms = terms;
	}

	public Optional<Double> getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(Optional<Double> taxPercent) {
		this.taxPercent = taxPercent;
	}

	public Optional<Double> getTaxAdd() {
		return taxAdd;
	}

	public void setTaxAdd(Optional<Double> taxAdd) {
		this.taxAdd = taxAdd;
	}

	public Optional<Double> getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(Optional<Double> taxBase) {
		this.taxBase = taxBase;
	}

	public Optional<Double> getTaxTotal() {
		return taxTotal;
	}

	public void setTaxTotal(Optional<Double> taxTotal) {
		this.taxTotal = taxTotal;
	}

	public Optional<Double> getBudgetTotal() {
		return budgetTotal;
	}

	public void setBudgetTotal(Optional<Double> budgetTotal) {
		this.budgetTotal = budgetTotal;
	}

}
