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
	
	public Tag setId (Integer id) {
		this.id = id;
		return this;
	}
	
	@Override
	public Integer getId() {		
		return id;
	}

	public Tag setDomain (Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getDomain () {
		return this.domain;
	}
	
	public Tag setType(byte type) {
		this.type = type;
		return this;
	}
	
	public byte getType() {
		return type;
	}
	
	public Tag setName (String name) {
		this.name = name;
		return this;
	}
	
	public String getName () {
		return this.name;
	}
	
	public Tag setColor (String color) {
		this.color = color;
		return this;
	}
	
	public String getColor () {
		return (this.color != null) ? this.color : "";
	}
}
