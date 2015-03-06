package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Enterprise implements Serializable {

	private static final long serialVersionUID = 8795104450454476889L;
	
	private Integer id;
	private int domain;
	private String document;
	private String surname;
	private String name;


	public Enterprise() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
