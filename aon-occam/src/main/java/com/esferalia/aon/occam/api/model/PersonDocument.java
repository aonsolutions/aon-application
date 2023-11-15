package com.esferalia.aon.occam.api.model;


import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;

@SuppressWarnings("serial")
public class PersonDocument implements Serializable{
	
	
	private String document;
	private Date birthDate;
	private String name ;
	private String firstSurName;
	private String secondSurName;
	private Date validity;
	private Date emission;
	private Country nationality;
	

	public String getDocument() {
		return document;
	}

	public PersonDocument setDocument(String document) {
		this.document = document;
		return this;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public PersonDocument setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public String getName() {
		return name;
	}

	public PersonDocument setName(String name) {
		this.name = name;
		return this;
	}

	public String getFirstSurname() {
		return firstSurName;
	}

	public PersonDocument setFirstSurname(String firstSurName) {
		this.firstSurName = firstSurName;
		return this;
	}

	public String getSecondSurname() {
		return secondSurName;
	}

	public PersonDocument setSecondSurname(String secondSurName) {
		this.secondSurName = secondSurName;
		return this;
	}

	public Date getValidity() {
		return validity;
	}

	public PersonDocument setValidity(Date validity) {
		this.validity = validity;
		return this;
	}

	public Date getIssueDate() {
		return emission;
	}

	public PersonDocument setIssueDate(Date emission) {
		this.emission = emission;
		return this;
	}

	public Country getNationality() {
		return nationality;
	}

	public PersonDocument setNationality(Country nationality) {
		this.nationality = nationality;
		return this;
	}
	
}
