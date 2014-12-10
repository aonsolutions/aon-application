package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Domain implements IsSerializable{

	String name;
	String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
