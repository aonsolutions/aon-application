package com.esferalia.aon.occam.api.model.scope;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;

@SuppressWarnings("serial")
public class UserScopeFull implements Serializable {
	
	Integer id;
	Integer domain;
	User user;
	Scope scope;

	public UserScopeFull() { }

	public Integer getDomain() {
		return domain;
	}

	public UserScopeFull setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public UserScopeFull setId(Integer id) {
		this.id = id;
		return this;
	}

	public User getUser() {
		return user;
	}

	public UserScopeFull setUser(User user) {
		this.user = user;
		return this;
	}

	public Scope getScope() {
		return scope;
	}

	public UserScopeFull setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	
}
