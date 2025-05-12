package com.esferalia.aon.occam.api.model.sales;

import java.io.Serializable;
import java.util.Date;

public class SalesParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private Date date;
	private Date deliveryDate;
	private Byte status;
	
	private int limit;
	private int offset;

	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public SalesParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public SalesParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getUser() {
		return user;
	}
	public SalesParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public SalesParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public SalesParams setDate(Date date) {
		this.date = date;
		return this;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public SalesParams setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public SalesParams setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public SalesParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public SalesParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public SalesParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public SalesParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
