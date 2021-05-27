package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Department implements Serializable{
	
	Integer id;
	Integer domain;
	String name;
	
	Boolean empty;
	
	public Integer getId() {
		return id;
	}
	public Department setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Department setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Department setName(String name) {
		this.name = name;
		return this;
	}
	
	public Boolean isEmpty(){
		return empty;
	}
	public Department setEmpty(Boolean empty){
		this.empty = empty;
		return this;
	}
}
