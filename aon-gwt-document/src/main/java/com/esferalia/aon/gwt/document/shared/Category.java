package com.esferalia.aon.gwt.document.shared;


import com.google.gwt.user.client.rpc.IsSerializable;

public class Category implements IsSerializable{

	Integer id;
	String name;
	String domain;
	
	public Category() {
	
	}
	
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
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
	
}
