package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class UserScope implements Serializable {
	
	private static final long serialVersionUID = -223623654302750540L;
	
	Integer id;
	Integer domain;
	Integer userId;
	Integer scope;

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
