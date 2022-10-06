package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.MimeType;

public class AuthAttach implements Serializable{

	Integer id;
	byte[] auth;
	byte[] data;
	AuthAttachType type;
	MimeType mimetype;
	
	public AuthAttach() {

	}
	
	public Integer getId() {
		return id;
}
	
	public AuthAttach setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public byte[] getAuth() {
		return auth;
	}
	
	public AuthAttach setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public AuthAttach setData(byte[] data) {
		this.data = data;
		return this;
	}

	public AuthAttachType getType() {
		return type;
	}
	
	public AuthAttach setType(AuthAttachType type) {
		this.type = type;
		return this;
	}
	
	public MimeType getMimetype() {
		return mimetype;
	}
	
	public AuthAttach setMimetype(MimeType mimetype) {
		this.mimetype = mimetype;
		return this;
	}
}
