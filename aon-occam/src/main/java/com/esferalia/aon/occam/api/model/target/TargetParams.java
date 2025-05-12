package com.esferalia.aon.occam.api.model.target;

import java.io.Serializable;
import java.util.Date;

public class TargetParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Date date;
	
	private int limit;
	private int offset;

	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public TargetParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public TargetParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getUser() {
		return user;
	}
	public TargetParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public TargetParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public TargetParams setDate(Date date) {
		this.date = date;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public TargetParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public TargetParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public TargetParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public TargetParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
