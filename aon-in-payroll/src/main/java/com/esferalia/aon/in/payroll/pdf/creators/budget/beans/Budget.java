package com.esferalia.aon.in.payroll.pdf.creators.budget.beans;

import java.util.HashMap;
import java.util.Optional;

public class Budget {

	private Optional<String> budget_number;
	private Optional<Client_data> client;
	private Optional<HashMap<Optional<String>, Optional<Double>>> products;
	
	public Budget(String budget_number, Client_data client, HashMap<Optional<String>, Optional<Double>> products) {
		super();
		this.budget_number = 	Optional.of(budget_number);
		this.client = 			Optional.of(client);
		this.products = 		Optional.of(products);
	}
		
}
