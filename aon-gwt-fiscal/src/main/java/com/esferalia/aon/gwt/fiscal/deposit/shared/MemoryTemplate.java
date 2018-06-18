package com.esferalia.aon.gwt.fiscal.deposit.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MemoryTemplate implements IsSerializable{
	Integer id;
	String name;
	String cif;
	
	public String getName() {
		return name;
	}
	public MemoryTemplate setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public MemoryTemplate setId(Integer id) {
		this.id = id;
		return this;
	}

}
