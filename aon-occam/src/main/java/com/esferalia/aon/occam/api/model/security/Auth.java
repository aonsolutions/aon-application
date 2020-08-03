package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class Auth implements Serializable {
	
	byte[] auth;
	String uuid;
	String email;
	String password;
	LinkedList<User> users;
	
	public Auth() { }

	public byte[] getAuth() {
		return auth;
	}

	public Auth setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}
	
	public String getUuid() {
		return uuid;
	}

	public Auth setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Auth setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Auth setPassword(String password) {
		this.password = password;
		return this;
	}

	public LinkedList<User> getUsers() {
		return users;
	}

	public Auth setUsers(LinkedList<User> users) {
		this.users = users;
		return this;
	}	
	
	
}
