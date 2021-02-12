package com.esferalia.aon.occam.api.model.security;

import org.json.JSONObject;

public class AuthDevice {
	public AuthDevice() {}

	private Integer id;
	private Auth auth;
	private String device_type;
	private String device_token;
	
	public Integer getId() {
		return id;
	}

	public AuthDevice setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public AuthDevice setAuth(Auth auth) {
		this.auth = auth;
		return this;
	}

	public AuthDevice setDeviceType(String device_type) {
		this.device_type = device_type;
		return this;
	}
	
	public AuthDevice setDeviceToken(String device_token) {
		this.device_token = device_token;
		return this;
	}


	public Auth getAuth() {
		return auth;
	}

	public String getDeviceType() {
		return device_type;
	}
	
	public String getDeviceToken() {
		return device_token;
	}


	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("auth", getAuth());
		json.put("device_type", getDeviceType());
		json.put("device_token", getDeviceToken());

		return json;
	}
}
