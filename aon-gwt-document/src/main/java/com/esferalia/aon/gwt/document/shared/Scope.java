package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Scope implements IsSerializable{

	Integer id;
	String name;
	String domain;
	Boolean isSon;
	Boolean isParent;
	
	public Scope() {

	}
	
	public Scope(Integer i){
		id = i;
	}
	
	public Integer getId() {
		return id;
	}
	public Scope setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Scope setName(String name) {
		this.name = name;
		return this;
	}
	public String getDomain() {
		return domain;
	}
	public Scope setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public Boolean getIsSon() {
		return isSon;
	}

	public Scope setIsSon(Boolean isSon) {
		this.isSon = isSon;
		return this;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public Scope setIsParent(Boolean isParent) {
		this.isParent = isParent;
		return this;
	}
	
}
