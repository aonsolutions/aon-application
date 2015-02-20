package com.esferalia.aon.gwt.template.server;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Warehouse implements IsSerializable{
	String name;
	Integer workplace;
	Integer id;
	Integer domainId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	
	
}
