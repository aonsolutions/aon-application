package com.esferalia.aon.occam.api.model.tariff;

import java.io.Serializable;

public class TariffParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Byte purchase;
	private Byte status;
	
	private int limit;
	private int offset;

	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public TariffParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public TariffParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public TariffParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public TariffParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Byte getPurchase() {
		return purchase;
	}
	public TariffParams setPurchase(Byte purchase) {
		this.purchase = purchase;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public TariffParams setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public TariffParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public TariffParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public TariffParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public TariffParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
	
	
}
