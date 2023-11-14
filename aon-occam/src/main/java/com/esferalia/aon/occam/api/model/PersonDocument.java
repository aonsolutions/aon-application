package com.esferalia.aon.occam.api.model;


import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;

public class PersonDocument {
	
	private String document;
	private Date birthDate;
	private String name ;
	private String firstSurName;
	private String secondSurName;
	private Date validity;
	private Date emission;
	private Country nationality;
	
	public PersonDocument() {}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstSurname() {
		return firstSurName;
	}

	public void setFirstSurname(String firstSurName) {
		this.firstSurName = firstSurName;
	}

	public String getSecondSurname() {
		return secondSurName;
	}

	public void setSecondSurname(String secondSurName) {
		this.secondSurName = secondSurName;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public Date getIssueDate() {
		return emission;
	}

	public void setIssueDate(Date emission) {
		this.emission = emission;
	}

	public Country getNationality() {
		return nationality;
	}

	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}
	
}
