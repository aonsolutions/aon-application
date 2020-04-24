package com.code.aon.webservice.issues;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class User {

	Integer id;
	String login;
	String type;
	String email;
	String alias;
	RegistryStatus status;
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
	
	public String getAlias() {
		return alias;
	}

	public User setAlias(String alias) {
		this.alias = alias;
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

	public RegistryStatus getStatus() {
		return status;
	}

	public User setStatus(RegistryStatus status) {
		this.status = status;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("login", getLogin());
		json.put("description", getLogin());
		json.put("email", email);
		json.put("workgroups", getWorkgroupsJSON());
		if(getStatus() != null){
			JSONObject jsStatus = new JSONObject();
			jsStatus.put("id", getStatus().value());
			jsStatus.put("name", getStatus().getDescription());
			json.put("customer_status", jsStatus);
			if(getStatus().equals(RegistryStatus.BLOCKED))
				json.put("description", "[B] " + getLogin());
			if(getStatus().equals(RegistryStatus.INACTIVE))
				json.put("description", "[I] " + getLogin());
		}
		if(getAlias() != null && !getAlias().equals("") && !getAlias().equals(" ")){
			String desc = json.get("description") + " (" + getAlias() + ")";
			json.put("description", desc);
		}
		return json;
	}
}
