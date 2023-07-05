package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRLoginToken implements Serializable {
	
	private static final long serialVersionUID = -620591373784755713L;
    
	private String id;
	private String token;
	private String expiration;
	private String user;
	private String creation;
	
	public Optional<String> getId() {
		return Optional.ofNullable(id);
	}
	public OCRLoginToken setId(String id) {
		this.id = id;
		return this;
	}
	
	public Optional<String> getToken() {
		return Optional.ofNullable(token);
	}
	public OCRLoginToken setToken(String token) {
		this.token = token;
		return this;
	}
	
	public Optional<String> getExpiration() {
		return Optional.ofNullable(expiration);
	}
	
	public OCRLoginToken setExpiration(String expiration) {
		this.expiration = expiration;
		return this;
	}
	
	public Optional<String> getCreation() {
		return Optional.ofNullable(creation);
	}
	
	public OCRLoginToken setCreation(String creation) {
		this.creation = creation;
		return this;
	}
	
	public Optional<String> getUser() {
		return Optional.ofNullable(user);
	}
	
	public OCRLoginToken setUser(String user) {
		this.user = user;
		return this;
	}
}
