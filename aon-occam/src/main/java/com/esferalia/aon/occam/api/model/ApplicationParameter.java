package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class ApplicationParameter implements Serializable {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private String name;
	private String value;

	public Integer getId() {
		return id;
	}
	public ApplicationParameter setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ApplicationParameter setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getName() {
		return name;
	}

	public ApplicationParameter setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ApplicationParameter setValue(String value) {
		this.value = value;
		return this;
	}

}