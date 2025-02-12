package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.ProductType;

public class ProductParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Integer category;
	private ProductType type;
	
	private int limit;
	private int offset;

	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public ProductParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public ProductParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public ProductParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public ProductParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getCategory() {
		return category;
	}
	public ProductParams setCategory(Integer category) {
		this.category = category;
		return this;
	}
	public ProductType getType() {
		return type;
	}
	public ProductParams setType(ProductType type) {
		this.type = type;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public ProductParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public ProductParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public ProductParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public ProductParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
	
	
}
