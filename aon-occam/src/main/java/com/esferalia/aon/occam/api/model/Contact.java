package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Serializable{

	Integer id;
	Integer domain;
	Integer userId;
	String displayName;
	Integer contactData;
	
	String email;
	
	public Contact() {}
	
	public Contact(Integer id, Integer domain, Integer userId, String displayName, Integer contactData) {
		this.id = id;
		this.domain = domain;
		this.userId = userId;
		this.displayName = displayName;
		this.contactData = contactData;
	}
	
	public Integer getId() {
		return id;
	}
	public Contact setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Contact setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getUserId() {
		return userId;
	}
	public Contact setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}
	public String getDisplayName() {
		return displayName;
	}
	public Contact setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
	public Integer getContactData() {
		return contactData;
	}
	public Contact setContactData(Integer contactData) {
		this.contactData = contactData;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Contact setEmail(String email) {
		this.email = email;
		return this;
	}
	
	
}
