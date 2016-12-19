package com.code.aon.webservice.issues;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {

	Integer id;
	String login;
	String type;
	String email;
	LinkedList<User> workgroups;
	
	public User() {}

	public User(String userName){
		setLogin(userName);
	}
	
	public User(com.esferalia.aon.occam.api.model.security.User u) {
		setId(u.getId());
		setLogin(u.getName());
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
	
	public String getEmail() {
		return email;
	}
	public User setEmail(String email) {
		this.email = email;
		return this;
	}
	
	
	public LinkedList<User> getWorkgroups() {
		return workgroups;
	}
	
	public JSONArray getWorkgroupsJSON() {
		JSONArray array = new JSONArray();
		LinkedList<User> list = getWorkgroups();
		if(list == null || list.isEmpty()) return array;
		else list.stream().forEach(u -> array.put(u.toJSON()));
		return array;
	}

	public User setWorkgroups(LinkedList<User> workgroups) {
		this.workgroups = workgroups;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("login", getLogin());
		json.put("email", email);
		json.put("workgroups", getWorkgroupsJSON());
		return json;
	}
}
