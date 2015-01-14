package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MailAccount implements IsSerializable{

	private Integer id;
	private String name;
	private String email;
	private String signature;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	
	
	
}
