package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AonConnection {
	
	private static final String SCHEMA = "schema";
	private static final String DOMAINS = "domains";
	private static final String USERS = "users";
	private static final String EMAIL = "email";

	private String schema;
	private LinkedList<Integer> domains;
	private LinkedList<Integer> users;
	private String email;
	
	public String getSchema() {
		return schema;
	}
	
	public AonConnection setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public LinkedList<Integer> getDomains() {
		return domains;
	}
	
	public AonConnection setDomains(LinkedList<Integer> domains) {
		this.domains = domains;
		return this;
	}
	
	public LinkedList<Integer> getUsers() {
		return users;
	}
	
	public AonConnection setUsers(LinkedList<Integer> users) {
		this.users = users;
		return this;
	}
	
	public String getEmail() {
		return email;
	}

	public AonConnection setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public JSONObject toJson() {
		return new JSONObject()
				.put(SCHEMA, getSchema())
				.put(DOMAINS, new JSONArray(getDomains().toArray(new Integer[getDomains().size()])))
				.put(USERS, new JSONArray(getUsers().toArray(new Integer[getUsers().size()])))
				.put(EMAIL, getEmail());
	}
	
	public static AonConnection parse(String json) {
		return parse(new JSONObject(json));
	}
	
	public static AonConnection parse(JSONObject json) {
		LinkedList<Integer> domains = new LinkedList<>();
		LinkedList<Integer> users = new LinkedList<>();
		json.getJSONArray(DOMAINS).forEach(r -> domains.add((Integer) r));
		json.getJSONArray(USERS).forEach(r -> users.add((Integer) r));
		return new AonConnection()
				.setSchema(json.getString(SCHEMA))
				.setDomains(domains)
				.setUsers(users)
				.setEmail(json.getString(EMAIL));
	}
	
}
