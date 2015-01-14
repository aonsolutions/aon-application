package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Contact implements IsSerializable{
	String displayName;
	String email;
	Integer user_id;
	Integer id;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
