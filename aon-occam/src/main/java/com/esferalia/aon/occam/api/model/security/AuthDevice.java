package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

public class AuthDevice implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public AuthDevice setDeviceType(DeviceType dv) {
		this.deviceType = dv;
		return this;
	}
	
	public AuthDevice setDeviceToken(String dv) {
		this.deviceToken = dv;
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

}
