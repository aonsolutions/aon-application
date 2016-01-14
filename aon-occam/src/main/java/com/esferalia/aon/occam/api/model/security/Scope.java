package com.esferalia.aon.occam.api.model.security;

public class Scope {
	
	String description;
	Integer domain;
	Integer id;
	
	public Scope() { }

	public String getDescription() {
		return description;
	}

	public Scope setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Scope setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Scope setId(Integer id) {
		this.id = id;
		return this;
	}
}
