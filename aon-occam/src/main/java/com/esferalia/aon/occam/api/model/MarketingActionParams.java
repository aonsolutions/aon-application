package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class MarketingActionParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	private Byte mediaType;
	
	private Date startDate;
	private Date endDate;

	private Double budget;
	private Double expense;
	
	private MarketingCampaign marketingCampaign;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public MarketingActionParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public MarketingActionParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public MarketingActionParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingActionParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Byte getMediaType() {
		return mediaType;
	}
	public MarketingActionParams setMediaType(Byte mediaType) {
		this.mediaType = mediaType;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public MarketingActionParams setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public MarketingActionParams setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public Double getBudget() {
		return budget;
	}
	public MarketingActionParams setBudget(Double budget) {
		this.budget = budget;
		return this;
	}
	public Double getExpense() {
		return expense;
	}
	public MarketingActionParams setExpense(Double expense) {
		this.expense = expense;
		return this;
	}
	public MarketingCampaign getMarketingCampaign() {
		return marketingCampaign;
	}
	public MarketingActionParams setMarketingCampaign(MarketingCampaign marketingCampaign) {
		this.marketingCampaign = marketingCampaign;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public MarketingActionParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public MarketingActionParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
