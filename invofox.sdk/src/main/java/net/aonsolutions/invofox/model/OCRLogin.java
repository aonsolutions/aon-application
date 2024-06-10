package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRLogin implements Serializable {
	
	private static final long serialVersionUID = -620591373784755713L;
    
	private String token;
	private String expiration;
	private boolean updatePassword;
	private OCRUser user;
	
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
	
	public boolean isUpdatePassword() {
		return updatePassword;
	}
	
	public OCRLogin setUpdatePassword(boolean updatePassword) {
		this.updatePassword = updatePassword;
		return this;
	}
	
	public Optional<OCRUser> getUser() {
		return Optional.ofNullable(user);
	}
	
	public OCRLogin setUser(OCRUser user) {
		this.user = user;
		return this;
	}
}
