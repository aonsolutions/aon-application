package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserWorkgroup implements Serializable {
	
	Integer id;
	Integer domain;
	Integer userId;
	Integer workgroup;

	public UserWorkgroup() { }

	public Integer getDomain() {
		return domain;
	}

	public UserWorkgroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public UserWorkgroup setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public UserWorkgroup setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getWorkgroup() {
		return workgroup;
	}

	public UserWorkgroup setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	
	
}
