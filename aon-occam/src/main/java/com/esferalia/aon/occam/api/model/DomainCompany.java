package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class DomainCompany implements Serializable {
	
	private static final long serialVersionUID = 6809124449291099756L;
	private String schema;
	private Domain domain;
	private Company company;
	
	public Domain getDomain() {
		return domain;
	}
	public DomainCompany setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Company getCompany() {
		return company;
	}
	public DomainCompany setCompany(Company company) {
		this.company = company;
		return this;
	}
	public String getSchema() {
		return schema;
	}
	public DomainCompany setSchema(String schema) {
		this.schema = schema;
		return this;
	}

}
