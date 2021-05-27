package com.code.aon.file.tax.model.MOD349.data;

public class Operator {

	private String country;
	private String document;
	private String name;
	private String key;
	private Double amount;

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		String description = "OPERATOR ";
		description += "Pais: ";
		description += country == null ? "NULL " : "'" + country + "'";
		description += " Doc.: ";
		description += document == null ? "NULL " : "'" + document + "''";
		description += " Nombre: ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}

}
