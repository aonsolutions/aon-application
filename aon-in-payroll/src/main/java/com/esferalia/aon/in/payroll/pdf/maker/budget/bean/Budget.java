package com.esferalia.aon.in.payroll.pdf.maker.budget.bean;

import java.util.ArrayList;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.ClientData.ClientDataBuilder;

public class Budget {

	private Optional<String>	  budgetNumber;
	private Optional<ClientData>  client;
	private ArrayList<BudgetItem> products;
	private ArrayList<Term>		  terms;
	private Optional<Double>	  taxPercent;
	private Optional<Double>	  taxAdd;
	private Optional<Double>	  taxBase;
	private Optional<Double>	  taxTotal;
	private Optional<Double>	  budgetTotal;

	/**
	 * Budget constructor [private]
	 */
	private Budget() {
	}

	/**
	 * Get Budget number or default value
	 * 
	 * @param defaultValue - the default value
	 * @return [String] Budget number
	 */
	public String getBudgetNumber(String defaultValue) {
		return budgetNumber.orElse(defaultValue);
	}

	/**
	 * Set budget number
	 * 
	 * @param budgetNumber - The budget number
	 */
	public void setBudgetNumber(String budgetNumber) {
		this.budgetNumber = Optional.ofNullable(budgetNumber);
	}

	/**
	 * get client data
	 * 
	 * @param defaultValue - The default value
	 * @return [ClientData] The client data
	 */
	public ClientData getClient(ClientData defaultValue) {
		return client.orElse(defaultValue);
	}

	/**
	 * Set client data
	 * 
	 * @param client - The client data
	 */
	public void setClient(ClientData client) {
		this.client = Optional.ofNullable(client);
	}

	/**
	 * Get the products
	 * 
	 * @return [Arraylist of BudgetItems]
	 */
	public ArrayList<BudgetItem> getProducts() {
		return products;
	}

	/**
	 * Get terms arrayList
	 * 
	 * @return [ArrayList of Terms]
	 */
	public ArrayList<Term> getTerms() {
		return terms;
	}

	/**
	 * Get tax percent or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [Double] Tax percent
	 */
	public Double getTaxPercent(Double defaultValue) {
		return taxPercent.orElse(defaultValue);
	}

	/**
	 * Set tax percent
	 * 
	 * @param taxPercent - The tax percent
	 */
	public void setTaxPercent(Double taxPercent) {
		this.taxPercent = Optional.ofNullable(taxPercent);
	}

	/**
	 * Get tax add or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [Double]
	 */
	public Double getTaxAdd(Double defaultValue) {
		return taxAdd.orElse(defaultValue);
	}

	/**
	 * Set tax add
	 * 
	 * @param taxAdd - The tax add
	 */
	public void setTaxAdd(Double taxAdd) {
		this.taxAdd = Optional.ofNullable(taxAdd);
	}

	/**
	 * Get tax base or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [Double]
	 */
	public Double getTaxBase(Double defaultValue) {
		return taxBase.orElse(defaultValue);
	}

	/**
	 * Set tax base
	 * 
	 * @param taxBase - The tax base
	 */
	public void setTaxBase(Double taxBase) {
		this.taxBase = Optional.ofNullable(taxBase);
	}

	/**
	 * Get tax total or default value
	 * 
	 * @param defaultValue -The default value
	 * @return [Double]
	 */
	public Double getTaxTotal(Double defaultValue) {
		return taxTotal.orElse(defaultValue);
	}

	/**
	 * Set tax total
	 * 
	 * @param taxTotal - The tax total
	 */
	public void setTaxTotal(Double taxTotal) {
		this.taxTotal = Optional.ofNullable(taxTotal);
	}

	/**
	 * Get the budget total or default value
	 * 
	 * @param defaultValue - The default value
	 * @return [Double]
	 */
	public Double getBudgetTotal(Double defaultValue) {
		return budgetTotal.orElse(defaultValue);
	}

	/**
	 * Set the budget total
	 * 
	 * @param budgetTotal - The budget total
	 */
	public void setBudgetTotal(Double budgetTotal) {
		this.budgetTotal = Optional.ofNullable(budgetTotal);
	}

	/**
	 * Builder for Budget objects
	 * 
	 * @author akrck02
	 *
	 */
	public static class BudgetBuilder {

		private Optional<String>	  budgetNumber;
		private Optional<ClientData>  client;
		private ArrayList<BudgetItem> products;
		private ArrayList<Term>		  terms;
		private Optional<Double>	  taxPercent;
		private Optional<Double>	  taxAdd;
		private Optional<Double>	  taxBase;
		private Optional<Double>	  taxTotal;
		private Optional<Double>	  budgetTotal;

		public BudgetBuilder() {
			this.budgetNumber = Optional.empty();
			this.client		  = Optional.of(new ClientDataBuilder().build());
			this.products	  = new ArrayList<>();
			this.terms		  = new ArrayList<>();
			this.taxPercent	  = Optional.empty();
			this.taxAdd		  = Optional.empty();
			this.taxBase	  = Optional.empty();
			this.taxTotal	  = Optional.empty();
			this.budgetTotal  = Optional.empty();
		}

		/**
		 * Set budget number
		 * 
		 * @param budgetNumber - The budget number
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setBudgetNumber(String budgetNumber) {
			this.budgetNumber = Optional.ofNullable(budgetNumber);
			return this;
		}

		/**
		 * Set the client data
		 * 
		 * @param client - The client data
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setClient(ClientData client) {
			this.client = Optional.ofNullable(client);
			return this;
		}

		/**
		 * Add a new product to the budget
		 * 
		 * @param product - The new product
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder addProduct(BudgetItem product) {
			this.products.add(product);
			return this;
		}

		/**
		 * Add a new term to the budget
		 * 
		 * @param term - The new term
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder addTerm(Term term) {
			this.terms.add(term);
			return this;
		}

		/**
		 * Set the tax percent
		 * 
		 * @param taxPercent - The tax percent
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setTaxPercent(Double taxPercent) {
			this.taxPercent = Optional.ofNullable(taxPercent);
			return this;
		}

		/**
		 * Set the tax add
		 * 
		 * @param taxAdd - The tax add
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setTaxAdd(Double taxAdd) {
			this.taxAdd = Optional.ofNullable(taxAdd);
			return this;
		}

		/**
		 * Set the tax base
		 * 
		 * @param taxBase - The tax base
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setTaxBase(Double taxBase) {
			this.taxBase = Optional.ofNullable(taxBase);
			return this;
		}

		/**
		 * Set the tax total
		 * 
		 * @param taxTotal - The tax total
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setTaxTotal(Double taxTotal) {
			this.taxTotal = Optional.ofNullable(taxTotal);
			return this;
		}

		/**
		 * Set the budget total
		 * 
		 * @param budgetTotal - The budget total
		 * @return [BudgetBuilder] The caller Object
		 */
		public BudgetBuilder setBudgetTotal(Double budgetTotal) {
			this.budgetTotal = Optional.ofNullable(budgetTotal);
			return this;
		}

		public Budget build() {
			Budget budget = new Budget();

			budget.budgetNumber	= this.budgetNumber;
			budget.client		= this.client;
			budget.products		= this.products;
			budget.terms		= this.terms;
			budget.taxPercent	= this.taxPercent;
			budget.taxAdd		= this.taxAdd;
			budget.taxBase		= this.taxBase;
			budget.taxTotal		= this.taxTotal;
			budget.budgetTotal	= this.budgetTotal;

			return budget;
		}

	}

}
