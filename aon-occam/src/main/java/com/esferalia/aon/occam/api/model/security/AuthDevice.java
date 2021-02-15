package com.esferalia.aon.occam.api.model.security;

import org.json.JSONObject;
public class AuthDevice {
	public AuthDevice() {}

	private Integer id;
	private byte[] auth;
	private DeviceType deviceType;
	private String deviceToken;
	
	public Integer getId() {
		return id;
	}

	public AuthDevice setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public AuthDevice setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}

	public AuthDevice setDeviceType(DeviceType device_type) {
		this.deviceType = device_type;
		return this;
	}
	
	public AuthDevice setDeviceToken(String device_token) {
		this.deviceToken = device_token;
		return this;
	}


	public byte[] getAuth() {
		return auth;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	public String getDeviceToken() {
		return deviceToken;
	}


	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("auth", getAuth());
		json.put("device_type", getDeviceType().value());
		json.put("device_token", getDeviceToken());
		return json;
	}
}
