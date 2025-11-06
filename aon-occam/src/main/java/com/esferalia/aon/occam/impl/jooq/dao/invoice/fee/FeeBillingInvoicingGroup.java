package com.esferalia.aon.occam.impl.jooq.dao.invoice.fee;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Customer;

class FeeBillingInvoicingGroup implements Serializable {
	
	private static final long serialVersionUID = -1155988586174019199L;
	
	private Integer id;
	private Integer domain;
	private Customer customer;
	private String description;
	private boolean customerGrouped;
	
	public Integer getId() {
		return id;
	}
	public FeeBillingInvoicingGroup setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public FeeBillingInvoicingGroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Customer getCustomer() {
		return customer;
	}
	public FeeBillingInvoicingGroup setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public FeeBillingInvoicingGroup setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isCustomerGrouped() {
		return customerGrouped;
	}
	public FeeBillingInvoicingGroup setCustomerGrouped(boolean customerGrouped) {
		this.customerGrouped = customerGrouped;
		return this;
	}
}
