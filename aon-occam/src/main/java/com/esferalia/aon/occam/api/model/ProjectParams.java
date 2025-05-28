package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class ProjectParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Integer projectType;
	
	private Integer registry;
	
	private Date date;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public ProjectParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public ProjectParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public ProjectParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public ProjectParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getProjectType() {
		return projectType;
	}
	public ProjectParams setProjectType(Integer projectType) {
		this.projectType = projectType;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public ProjectParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public ProjectParams setDate(Date date) {
		this.date = date;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public ProjectParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public ProjectParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public ProjectParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public ProjectParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
