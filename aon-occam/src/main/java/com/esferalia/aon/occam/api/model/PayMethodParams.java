package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class PayMethodParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	private Byte type;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public PayMethodParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public PayMethodParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public PayMethodParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public PayMethodParams setType(Byte type) {
		this.type = type;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public PayMethodParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public PayMethodParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public PayMethodParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public PayMethodParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public PayMethodParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
