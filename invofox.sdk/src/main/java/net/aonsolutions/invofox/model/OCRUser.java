package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class OCRUser implements Serializable {
	
	private static final long serialVersionUID = -620591373784755713L;
    
	private String id;
	private String account;
	private String name;
	private String email;
	private String lang;
	private List<String> securityGroups;

	public Optional<String> getId() {
		return Optional.ofNullable(id);
	}
	public OCRUser setId(String id) {
		this.id = id;
		return this;
	}
	
	public Optional<String> getAccount() {
		return Optional.ofNullable(account);
	}
	
	public OCRUser setAccount(String account) {
		this.account = account;
		return this;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	
	public OCRUser setName(String name) {
		this.name = name;
		return this;
	}
	
	public Optional<String> getEmail() {
		return Optional.ofNullable(email);
	}
	
	public OCRUser setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public Optional<String> getLang() {
		return Optional.ofNullable(lang);
	}
	
	public OCRUser setLang(String lang) {
		this.lang = lang;
		return this;
	}
	
	public List<String> getSecurityGroups() {
		if(securityGroups == null) {
			securityGroups = new LinkedList<>();
		}
		return securityGroups;
	}
	
	public OCRUser setSecurityGroups(List<String> securityGroups) {
		this.securityGroups = securityGroups;
		return this;
	}
	
}
