package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class Auth implements Serializable {
	
	byte[] auth;
	String uuid;
	String email;
	String password;
	String name;
	String surname;
	String document;
	String phone;
	String schema;
	String avatar;

	@Deprecated
	AuthAttach attach;
	List<AuthDevice> devices;
	
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

	public String getName() {
		return name;
	}

	public Auth setName(String name) {
		this.name = name;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public Auth setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	public String getFullname() {
		return AonStringUtils.isBlank(name) ? "" : name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + 
				(AonStringUtils.isBlank(surname) ? "" : " " + surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase());
	}

	public String getDocument() {
		return document;
	}

	public Auth setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public Auth setPhone(String phone) {
		this.phone = phone;
		return this;
	}	
	
	public String getSchema() {
		return schema;
	}
	
	public Auth setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public Auth setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}
	
	public List<AuthDevice> getDevices() {
		if(devices == null) {
			devices = new LinkedList<>();
		}
		return devices;
	}
	
	public Auth setDevices(List<AuthDevice> devices) {
		this.devices = devices;
		return this;
	}
	
	public AuthAttach getAttach() {
		if(attach == null) {
			attach = new AuthAttach();
		}
		return attach;
	}
	
	public Auth setAttach(AuthAttach attach) {
		this.attach = attach;
		return this;
	}
	
	public boolean isEmpty() {
		return getAuth() == null && getUuid() == null
			&& getEmail() == null && getPassword() == null
			&& getName() == null && getSurname() == null
			&& getDocument() == null && getPhone() == null;
	}
}
