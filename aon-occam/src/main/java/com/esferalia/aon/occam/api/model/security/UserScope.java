package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserScope implements Serializable {
	
	Integer id;
	Integer domain;
	Integer userId;
	Integer scope;

	public UserScope() { }

	public Integer getDomain() {
		return domain;
	}

	public UserScope setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public UserScope setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public UserScope setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public UserScope setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	
}
