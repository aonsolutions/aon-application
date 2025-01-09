package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class MarketingActionTargetMassiveParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Integer scope;
	private Byte entity;
	private Byte advertising;
	private Integer projectType;
	private Integer projectActivity;
	private Byte status;
	private boolean customer = false;
	private Integer mkAction;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public MarketingActionTargetMassiveParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public MarketingActionTargetMassiveParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getUser() {
		return user;
	}
	public MarketingActionTargetMassiveParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public MarketingActionTargetMassiveParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public MarketingActionTargetMassiveParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Byte getEntity() {
		return entity;
	}
	public MarketingActionTargetMassiveParams setEntity(Byte entity) {
		this.entity = entity;
		return this;
	}
	public Byte getAdvertising() {
		return advertising;
	}
	public MarketingActionTargetMassiveParams setAdvertising(Byte advertising) {
		this.advertising = advertising;
		return this;
	}
	public Integer getProjectType() {
		return projectType;
	}
	public MarketingActionTargetMassiveParams setProjectType(Integer projectType) {
		this.projectType = projectType;
		return this;
	}
	public Integer getProjectActivity() {
		return projectActivity;
	}
	public MarketingActionTargetMassiveParams setProjectActivity(Integer projectActivity) {
		this.projectActivity = projectActivity;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public MarketingActionTargetMassiveParams setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public boolean isCustomer() {
		return customer;
	}
	public MarketingActionTargetMassiveParams setCustomer(boolean customer) {
		this.customer = customer;
		return this;
	}
	public Integer getMkAction() {
		return mkAction;
	}
	public MarketingActionTargetMassiveParams setMkAction(Integer mkAction) {
		this.mkAction = mkAction;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public MarketingActionTargetMassiveParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public MarketingActionTargetMassiveParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public MarketingActionTargetMassiveParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public MarketingActionTargetMassiveParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
	
	
}
