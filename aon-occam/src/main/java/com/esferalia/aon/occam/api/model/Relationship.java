package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Relationship implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Domain domain;
	private String description;
	
	public Integer getId() {
		return id;
	}
	
	public Relationship setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public Relationship setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Relationship setDescription(String description) {
		this.description = description;
		return this;
	}
}
