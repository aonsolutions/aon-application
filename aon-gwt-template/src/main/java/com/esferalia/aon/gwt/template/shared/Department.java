package com.esferalia.aon.gwt.template.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Department implements IsSerializable{
	Integer id;
	String name;
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
}
