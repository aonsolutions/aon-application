package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class GroupEntitie implements Serializable {

	private static final long serialVersionUID = 7066246531474794602L;
	
	private String document;
	private String country;

	public String getDocument() {
		return document;
	}

	public GroupEntitie setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public GroupEntitie setCountry(String country) {
		this.country = country;
		return this;
	}
	
}
