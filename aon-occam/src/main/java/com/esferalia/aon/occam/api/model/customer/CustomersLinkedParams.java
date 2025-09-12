package com.esferalia.aon.occam.api.model.customer;

import java.io.Serializable;

public class CustomersLinkedParams implements Serializable {

	private static final long serialVersionUID = 2305550245539284689L;
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private String query;
	private Byte[] customerStatus;
	
	private boolean isSig = false;
	
	private int offset;
	private int limit;
	
	public CustomersLinkedParams() {
		super();
	}

	public String getDomainName() {
		return domainName;
	}

	public CustomersLinkedParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public CustomersLinkedParams setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public String getUser() {
		return user;
	}

	public CustomersLinkedParams setUser(String user) {
		this.user = user;
		return this;
	}

	public String getQuery() {
		return query;
	}

	public CustomersLinkedParams setQuery(String query) {
		this.query = query;
		return this;
	}

	public Byte[] getCustomerStatus() {
		return customerStatus;
	}

	public CustomersLinkedParams setCustomerStatus(Byte[] customerStatus) {
		this.customerStatus = customerStatus;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public CustomersLinkedParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public CustomersLinkedParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public boolean isSig() {
		return isSig;
	}

	public CustomersLinkedParams setSig(boolean isSig) {
		this.isSig = isSig;
		return this;
	}
	
	
}
