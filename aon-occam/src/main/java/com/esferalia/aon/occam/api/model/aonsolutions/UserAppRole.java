package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

public class UserAppRole {

	private Integer id;
	private Integer domain;
	private AonApp app;
	private Integer user;
	private AonRole role;
	
	public UserAppRole() {
		
	}

	public Integer getId() {
		return id;
	}

	public UserAppRole setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public UserAppRole setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public AonApp getApp() {
		return app;
	}

	public UserAppRole setApp(AonApp app) {
		this.app = app;
		return this;
	}

	public Integer getUser() {
		return user;
	}

	public UserAppRole setUser(Integer user) {
		this.user = user;
		return this;
	}

	public AonRole getRole() {
		return role;
	}

	public UserAppRole setRole(AonRole role) {
		this.role = role;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain());
		json.put("user", getUser());
		if(getApp() != null) json.put("app", getApp().name());
		if(getApp() != null) json.put("role", getRole().name());
		return json;
	}
}
