package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasId;

public class User implements Serializable, HasId<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private String name;
	private String enterprise;
	
	public User() {
		
	}
	
	public void setId (Integer id) {
		this.id = id;
	}
	
	public void setDomain (Integer domain) {
		this.domain = domain;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public void setEnterprise (String enterprise) {
		this.enterprise = enterprise;
	}

	@Override
	public Integer getId() {
		return this.id;
	}
	
	public Integer getDomain () {
		return this.domain;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String getEnterprise () {
		return this.enterprise;
	}
}
