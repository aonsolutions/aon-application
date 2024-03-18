package aon.solutions.gpt.cli;

import aon.solutions.gpt.Customer.CustomerFilter;

public class CustomerCli {
	
	public static void main(String[] args) {
		CustomerFilter customerFilter = new CustomerFilter();
		customerFilter.withCif("");
		customerFilter.withDni("");
		
	}

}
