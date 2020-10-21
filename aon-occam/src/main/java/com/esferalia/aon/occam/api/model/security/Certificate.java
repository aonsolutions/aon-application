package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

public class Certificate implements Serializable {
	
	private String type;
	private String password;
	private byte [] certificate;
	
	public String getType() {
		return type;
	}
	
	public String getPassword() {
		return password;
	}
	
	public byte[] getCertificate() {
		return certificate;
	}
	
	public Certificate setType(String type) {
		this.type = type;
		return this;
	}

	public Certificate setPassword(String password) {
		this.password = password;
		return this;
	}

	public Certificate setCertificate(byte certificate []) {
		this.certificate = certificate;
		return this;
	}

}
