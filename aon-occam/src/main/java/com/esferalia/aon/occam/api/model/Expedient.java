package com.esferalia.aon.occam.api.model;

import java.util.Date;

public class Expedient {

	private String type;
	private Integer year;
	private String concept;
	private Double base;
	private String document;
	private Date date;
	private Integer expendient;
	private String alias;
	private String name;
	
	public Expedient() {

}

	public String getType() {
		return type;
	}

	public Expedient setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getYear() {
		return year;
	}

	public Expedient setYear(Integer year) {
		this.year = year;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public Expedient setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public Expedient setBase(Double base) {
		this.base = base;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Expedient setDocument(String document) {
		this.document = document;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public Expedient setDate(Date date) {
		this.date = date;
		return this;
	}

	public Integer getExpendient() {
		return expendient;
	}

	public Expedient setExpendient(Integer expendient) {
		this.expendient = expendient;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}

	public Expedient setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public Expedient setName(String name) {
		this.name = name;
		return this;
	}
}