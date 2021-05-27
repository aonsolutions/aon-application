package com.esferalia.aon.occam.api.model.office;

public class Identification {
	
	private Integer id;
	private String name;
	private String document;
	private String alias;
	private String value;
	
	public Identification() {
		
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDocument(String document) {
		this.document = document;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDocument() {
		return document;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String getValue() {
		return value;
	}
}
