package com.esferalia.aon.occam.api.model.product;

public class Brand {
	
	Integer id;
	Integer domain;
	String name;
	
	public Integer getId() {
		return id;
	}
	public Brand setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Brand setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Brand setName(String name) {
		this.name = name;
		return this;
	}

	
}
