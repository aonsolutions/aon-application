package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.HashMap;

public class Scope implements Serializable {
	
	private static final long serialVersionUID = -4693465020389997927L;
	
	private String description;
	private Integer domain;
	private Integer id;
	
	// Only fill in SecurityDAO.getUserScopesByUserList
	private HashMap<Integer, String> scopeDomains = new HashMap<Integer, String>();
	
	public Integer getId() {
		return id;
	}
	public Scope setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Scope setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Scope setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public HashMap<Integer, String> getScopeDomains() {
		return scopeDomains;
	}
	public Scope setScopeDomains(HashMap<Integer, String> scopeDomains) {
		this.scopeDomains = scopeDomains;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null 
			&& getDomain() == null 
			&& getDescription() == null;
	}


}
