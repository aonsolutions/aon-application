package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasId;

public class Tag implements Serializable, HasId<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private String name;
	private String color;
	
	public Tag() {
		
	}
	
	public void setId (Integer id) {
		this.id = id;
	}
	
	@Override
	public Integer getId() {		
		return id;
	}

	public void setDomain (Integer domain) {
		this.domain = domain;
	}
	
	public Integer getDomain () {
		return this.domain;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void setColor (String color) {
		this.color = color;
	}
	
	public String getColor () {
		return this.color;
	}
}
