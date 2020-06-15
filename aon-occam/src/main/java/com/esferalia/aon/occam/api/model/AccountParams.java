package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class AccountParams implements Serializable{
	
	private static final long serialVersionUID = -5121339197116533299L;
	
	private String domainName; 
	private int domain;
	private String user;
	private Integer id;
	private String code;
	private String description;
	private String alias;
	private Byte level;
	private Boolean active;
	private String costCenter;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public AccountParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public AccountParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public AccountParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public AccountParams setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getCode() {
		return code;
	}
	public AccountParams setCode(String code) {
		this.code = code;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public AccountParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public AccountParams setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public Byte getLevel() {
		return level;
	}
	public AccountParams setLevel(Byte level) {
		this.level = level;
		return this;
	}
	public Boolean getActive() {
		return active;
	}
	public AccountParams setActive(Boolean active) {
		this.active = active;
		return this;
	}
	public String getCostCenter() {
		return costCenter;
	}
	public AccountParams setCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public AccountParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public AccountParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
