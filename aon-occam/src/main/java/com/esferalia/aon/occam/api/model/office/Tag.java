package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.HasId;

public class Tag implements Serializable, HasId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private byte type;
	private String name;
	private String color;
	
	public Tag() {
		
	}
	
	public void setId (Integer id) {
		this.id = id;
	}
	
	@Override
	public Integer getId() {		
		return id;
	}

	public void setDomain (Integer domain) {
		this.domain = domain;
	}
	
	public Integer getDomain () {
		return this.domain;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void setColor (String color) {
		this.color = color;
	}
	
	public String getColor () {
		return (this.color != null) ? this.color : "";
	}
}
