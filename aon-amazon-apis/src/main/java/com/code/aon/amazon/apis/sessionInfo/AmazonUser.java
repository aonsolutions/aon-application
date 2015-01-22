package com.code.aon.amazon.apis.sessionInfo;

public class AmazonUser {

	String user_id;
	String name;
	String email;
	String state;
	
	public AmazonUser(String user_id,String name,String email) {
		this.user_id = user_id;
		this.name = name;
		this.email = email;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
