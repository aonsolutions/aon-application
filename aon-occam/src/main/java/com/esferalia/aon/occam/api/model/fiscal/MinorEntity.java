package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class MinorEntity implements Serializable  {
	
	private static final long serialVersionUID = -5460280563691126891L;
		
	private String document;
	private String name;
	
	public String getDocument() {
		return document;
	}
	public MinorEntity setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}
	public MinorEntity setName(String name) {
		this.name = name;
		return this;
	}
	
}
