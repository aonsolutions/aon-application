package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.HasId;

public class User implements Serializable, HasId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private String name;
	private String login;
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
	
	public void setLogin(String login) {
		this.login = login;
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
	
	public String getLogin() {
		return this.login;
	}
	
	public String getEnterprise () {
		return this.enterprise;
	}
}
