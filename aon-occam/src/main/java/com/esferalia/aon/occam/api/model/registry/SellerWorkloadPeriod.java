package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class SellerWorkloadPeriod  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer customers;
	private Integer customerFees;
	private Double amount;
	
	public SellerWorkloadPeriod() {
		super();
	}
	
	public Integer getCustomers() {
		return customers;
	}

	public SellerWorkloadPeriod setCustomers(Integer customers) {
		this.customers = customers;
		return this;
	}

	public Integer getCustomerFees() {
		return customerFees;
	}

	public SellerWorkloadPeriod setCustomerFees(Integer customerFees) {
		this.customerFees = customerFees;
		return this;
	}

	public Double getAmount() {
		return amount;
	}

	public SellerWorkloadPeriod setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
}