package com.esferalia.aon.gwt.fiscal.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MemoryTemplate implements IsSerializable{
	Integer id;
	String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
