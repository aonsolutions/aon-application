package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class MarketingActionTargetParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private MarketingAction marketingAction;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public MarketingActionTargetParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public MarketingActionTargetParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public MarketingActionTargetParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingActionTargetParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public MarketingAction getMarketingAction() {
		return marketingAction;
	}
	public MarketingActionTargetParams setMarketingAction(MarketingAction marketingAction) {
		this.marketingAction = marketingAction;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public MarketingActionTargetParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public MarketingActionTargetParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
