package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class QuestionParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String alias;
	private Byte type;
	private Byte active;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public QuestionParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public QuestionParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public QuestionParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public QuestionParams setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public QuestionParams setType(Byte type) {
		this.type = type;
		return this;
	}
	public Byte getActive() {
		return active;
	}
	public QuestionParams setActive(Byte active) {
		this.active = active;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public QuestionParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public QuestionParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
