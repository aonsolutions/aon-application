package com.esferalia.aon.occam.api.model.customer;

import java.io.Serializable;

public class CustomersDomainSyncParams implements Serializable {

	private static final long serialVersionUID = 2305550245539284689L;
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private String query;
	
	private boolean isSig = false;
	
	private int offset;
	private int limit;
	
	public CustomersDomainSyncParams() {
		super();
	}

	public String getDomainName() {
		return domainName;
	}

	public CustomersDomainSyncParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public CustomersDomainSyncParams setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public String getUser() {
		return user;
	}

	public CustomersDomainSyncParams setUser(String user) {
		this.user = user;
		return this;
	}

	public String getQuery() {
		return query;
	}

	public CustomersDomainSyncParams setQuery(String query) {
		this.query = query;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public CustomersDomainSyncParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public CustomersDomainSyncParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public boolean isSig() {
		return isSig;
	}

	public CustomersDomainSyncParams setSig(boolean isSig) {
		this.isSig = isSig;
		return this;
	}
	
	
}
