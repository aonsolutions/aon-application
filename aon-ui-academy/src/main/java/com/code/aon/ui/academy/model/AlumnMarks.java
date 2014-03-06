package com.code.aon.ui.academy.model;

import java.io.Serializable;

import com.code.aon.academy.Mark;
import com.code.aon.common.AonVersion;
import com.code.aon.customer.Customer;

public class AlumnMarks implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Customer customer;
	
	private Mark[] values;

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

	public Mark[] getValues() {
		return values;
	}

	public void setValues(Mark[] values) {
		this.values = values;
	}
	
}
