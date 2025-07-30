package com.esferalia.aon.occam.api.model.scope;

import java.io.Serializable;

public class ScopeParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	
	public ScopeParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	
	public ScopeParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	
	public ScopeParams setUser(String user) {
		this.user = user;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ScopeParams setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public ScopeParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public ScopeParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public ScopeParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public ScopeParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
