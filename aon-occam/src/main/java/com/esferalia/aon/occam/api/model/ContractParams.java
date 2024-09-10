package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class ContractParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;

	private String description;
	
	private Byte active;
	private String tc2;
	private Integer workplace;
	private Date from;
	private Date to;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	public ContractParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public ContractParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public ContractParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public ContractParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public ContractParams setActive(Byte active) {
		this.active = active;
		return this;
	}
	public String getTc2() {
		return tc2;
	}
	public ContractParams setTc2(String tc2) {
		this.tc2 = tc2;
		return this;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public ContractParams setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	public Date getFrom() {
		return from;
	}
	public ContractParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public ContractParams setTo(Date to) {
		this.to = to;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public ContractParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public ContractParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public ContractParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public boolean isAsc() {
		return asc;
	}
	public ContractParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
