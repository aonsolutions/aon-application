package com.esferalia.aon.occam.api.model.tag;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.TagType;

public class TagParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	
	private TagType tagType;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public String getDomainName() {
		return domainName;
	}
	
	public TagParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	
	public TagParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	
	public TagParams setUser(String user) {
		this.user = user;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public TagParams setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public TagType getTagType() {
		return tagType;
	}

	public TagParams setTagType(TagType tagType) {
		this.tagType = tagType;
		return this;
	}

	public int getLimit() {
		return limit;
	}
	
	public TagParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public TagParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public TagParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public TagParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
	
}
