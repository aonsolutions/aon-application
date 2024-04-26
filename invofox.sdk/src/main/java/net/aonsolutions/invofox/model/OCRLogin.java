package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRLogin implements Serializable {
	
	private static final long serialVersionUID = -620591373784755713L;
    
	private String token;
	private String expiration;
	
	public Optional<String> getToken() {
		return Optional.ofNullable(token);
	}
	public OCRLogin setToken(String token) {
		this.token = token;
		return this;
	}
	
	public Optional<String> getExpiration() {
		return Optional.ofNullable(expiration);
	}
	
	public OCRLogin setExpiration(String expiration) {
		this.expiration = expiration;
		return this;
	}
}
