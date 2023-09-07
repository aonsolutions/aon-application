package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.List;

public class CustomerParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private Integer domain;
	
	private String customer;
	private Byte customerStatus;

	private Integer limit;
	private Integer offset;
	
	private List<Integer> customerIds;
	
	public CustomerParams() {
		super();
	}
	
	public Integer getDomain() {
		return domain;
	}
	public CustomerParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getCustomer() {
		return customer;
	}
	public CustomerParams setCustomer(String customer) {
		this.customer = customer;
		return this;
	}
	public Byte getCustomerStatus() {
		return customerStatus;
	}
	public CustomerParams setCustomerStatus(Byte customerStatus) {
		this.customerStatus = customerStatus;
		return this;
	}
	public Integer getLimit() {
		return limit;
	}
	public CustomerParams setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public Integer getOffset() {
		return offset;
	}
	public CustomerParams setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}
	public List<Integer> getCustomerIds() {
		return this.customerIds;
	}
	public void setCustomerIds(List<Integer> ids) {
		this.customerIds = ids;
	}
}
