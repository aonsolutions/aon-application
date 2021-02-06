package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Workgroup;

@SuppressWarnings("serial")
public class UserWorkgroup implements Serializable {
	
	Integer id;
	Integer domain;
	Integer userId;
	Workgroup workgroup;

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

	public Workgroup getWorkgroup() {
		return workgroup;
	}

	public UserWorkgroup setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	
	
}
