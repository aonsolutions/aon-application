package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class Segment implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private String name;
	
	public Integer getId() {
		return id;
	}
	public Segment setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Segment setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Segment setName(String name) {
		this.name = name;
		return this;
	}
	
	public boolean isEmpty() {
		return this.getId()==null;
	}
	
	
}
