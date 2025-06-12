package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.List;

public class CustomerParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Byte status;
	
	private List<Integer> customerIds;

	private Integer limit;
	private Integer offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public CustomerParams() {
		super();
	}

	public String getDomainName() {
		return domainName;
	}

	public CustomerParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public CustomerParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public CustomerParams setUser(String user) {
		this.user = user;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public CustomerParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public CustomerParams setStatus(Byte status) {
		this.status = status;
		return this;
	}

	public List<Integer> getCustomerIds() {
		return customerIds;
	}

	public CustomerParams setCustomerIds(List<Integer> customerIds) {
		this.customerIds = customerIds;
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

	public String getOrderBy() {
		return orderBy;
	}

	public CustomerParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public boolean isAsc() {
		return asc;
	}

	public CustomerParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
