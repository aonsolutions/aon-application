package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class SellerParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String name;
	private String alias;
	private String document;
	private Integer scope;
	private Byte active;
	
	private String description;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public SellerParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public SellerParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public SellerParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getName() {
		return name;
	}
	public SellerParams setName(String name) {
		this.name = name;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public SellerParams setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public SellerParams setDocument(String document) {
		this.document = document;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public SellerParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public SellerParams setActive(Byte active) {
		this.active = active;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public SellerParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public SellerParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public SellerParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public SellerParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public SellerParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
