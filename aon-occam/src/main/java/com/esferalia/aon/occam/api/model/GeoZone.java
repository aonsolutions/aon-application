package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class GeoZone implements Serializable {

	private static final long serialVersionUID = -1293760694660980945L;
	
	private Integer id;
	private int domain;
	private String name;
	private String code;
	private boolean system;

	public Integer getId() {
		return id;
	}

	public GeoZone setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public GeoZone setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}

	public GeoZone setCode(String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}

	public GeoZone setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSystem() {
		return system;
	}

	public GeoZone setSystem(boolean system) {
		this.system = system;
		return this;
	}
	
}
