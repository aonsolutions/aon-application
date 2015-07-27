package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MemoryFiles implements Serializable, IsSerializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Integer id;
	String name;
	Boolean bool;
	
	
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
	public Boolean getBool() {
		return bool;
	}
	public void setBool(Boolean bool) {
		this.bool = bool;
	}
	
	

}
