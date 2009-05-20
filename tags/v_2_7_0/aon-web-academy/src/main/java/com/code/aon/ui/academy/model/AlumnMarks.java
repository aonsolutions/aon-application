package com.code.aon.ui.academy.model;

import com.code.aon.customer.Customer;

public class AlumnMarks {

	private Customer customer;
	
	private Object[] values;

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
	
}
