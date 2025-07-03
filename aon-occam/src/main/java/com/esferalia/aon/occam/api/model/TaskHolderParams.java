package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class TaskHolderParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Boolean status;
	private Integer workgroup;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public TaskHolderParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public TaskHolderParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public TaskHolderParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public TaskHolderParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Boolean getStatus() {
		return status;
	}
	public TaskHolderParams setStatus(Boolean status) {
		this.status = status;
		return this;
	}
	public Integer getWorkgroup() {
		return workgroup;
	}
	public TaskHolderParams setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public TaskHolderParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public TaskHolderParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public TaskHolderParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public TaskHolderParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
