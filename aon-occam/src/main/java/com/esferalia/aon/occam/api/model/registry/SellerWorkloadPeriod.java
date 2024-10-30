package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class SellerWorkloadPeriod  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer customers;
	private Integer customerFees;
	private Integer salaries;
	private Double netAmount;
	private Double totalAmount;
	
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

	public Integer getSalaries() {
		return salaries;
	}

	public SellerWorkloadPeriod setSalaries(Integer salaries) {
		this.salaries = salaries;
		return this;
	}

	public Double getNetAmount() {
		return netAmount;
	}

	public SellerWorkloadPeriod setNetAmount(Double amount) {
		this.netAmount = amount;
		return this;
	}
	
	public Double getTotalAmount() {
		return totalAmount;
	}

	public SellerWorkloadPeriod setTotalAmount(Double amount) {
		this.totalAmount = amount;
		return this;
	}
}