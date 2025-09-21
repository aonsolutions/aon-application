package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class DomainSigAddInfo implements Serializable{

	private static final long serialVersionUID = 3060775131092037556L;
	
	private Integer registry;
	private String domainGroup;
	private String domainId;
	private String domainName;
	private String domainSchema;
	private String domainType;
	
	public DomainSigAddInfo() {
		super();
	}

	public Integer getRegistry() {
		return registry;
	}

	public DomainSigAddInfo setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public String getDomainGroup() {
		return domainGroup;
	}

	public DomainSigAddInfo setDomainGroup(String domainGroup) {
		this.domainGroup = domainGroup;
		return this;
	}

	public String getDomainId() {
		return domainId;
	}

	public DomainSigAddInfo setDomainId(String domainId) {
		this.domainId = domainId;
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public DomainSigAddInfo setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public String getDomainSchema() {
		return domainSchema;
	}

	public DomainSigAddInfo setDomainSchema(String domainSchema) {
		this.domainSchema = domainSchema;
		return this;
	}

	public String getDomainType() {
		return domainType;
	}

	public DomainSigAddInfo setDomainType(String domainType) {
		this.domainType = domainType;
		return this;
	}
	
}
