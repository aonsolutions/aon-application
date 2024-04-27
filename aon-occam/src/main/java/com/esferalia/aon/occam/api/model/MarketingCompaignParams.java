package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class MarketingCompaignParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	private Integer scope;
	private Byte active;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public MarketingCompaignParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public MarketingCompaignParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public MarketingCompaignParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingCompaignParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public MarketingCompaignParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public MarketingCompaignParams setActive(Byte active) {
		this.active = active;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public MarketingCompaignParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public MarketingCompaignParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
