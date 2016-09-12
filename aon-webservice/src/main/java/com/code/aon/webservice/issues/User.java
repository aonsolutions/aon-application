package com.code.aon.webservice.issues;

import org.json.JSONObject;

public class User {

	Integer id;
	String login;
	String type;
	
	public User() {}

	public User(com.esferalia.aon.occam.api.model.security.User u) {
		this.setId(u.getId());
		this.setLogin(u.getName());
	}
	
	public Integer getId() {
		return id;
	}
	public User setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getLogin() {
		return login;
	}
	public User setLogin(String login) {
		this.login = login;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("login", getLogin());
		return json;
	}
}
