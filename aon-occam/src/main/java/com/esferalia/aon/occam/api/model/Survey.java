package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.security.Scope;

public class Survey implements Serializable {
	
	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer id;
	private Domain domain;
	private Scope scope;
	private boolean active;
	private Date creationDate;
	private String description;
	
	public Integer getId() {
		return id;
	}
	public Survey setId(Integer id) {
		this.id = id;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public Survey setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Scope getScope() {
		return scope;
	}
	public Survey setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public Survey setActive(boolean active) {
		this.active = active;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Survey setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Survey setDescription(String description) {
		this.description = description;
		return this;
	}
	
	
}
