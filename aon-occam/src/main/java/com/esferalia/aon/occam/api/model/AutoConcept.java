package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class AutoConcept implements Serializable {

	private static final long serialVersionUID = 5527170796039531884L;
	
	private Integer id;
	private Integer domain;
	private String description;

	public Integer getId() {
		return id;
	}

	public AutoConcept setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AutoConcept setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AutoConcept setDescription(String description) {
		this.description = description;
		return this;
	}
	
}
