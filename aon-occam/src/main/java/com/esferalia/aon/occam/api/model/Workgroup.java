package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Workgroup implements Serializable {
	
	private static final long serialVersionUID = -6673378118872331822L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private Byte status;

	public Byte getStatus() {
		return status;
	}
	public Workgroup setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Workgroup setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Workgroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Workgroup setId(Integer id) {
		this.id = id;
		return this;
	}
}
