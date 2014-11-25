package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tag implements IsSerializable {

	Integer id;
	String name;
	String domain;
	Boolean isSon;
	Boolean isParent;
	public Tag() {
	
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

	public Boolean getIsSon() {
		return isSon;
	}

	public void setIsSon(Boolean isSon) {
		this.isSon = isSon;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
}
