package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.HasId;

public class Repository implements Serializable, HasId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private String name;
	private String description;
	
	public Repository() {
		
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return this.id;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public Integer getDomain() {
		return this.domain;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
