package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Warehouse implements Serializable {
	
	Integer id;
	Integer domain;
	Integer department;
	String name;
	Integer workplace;
	boolean active;

	public Warehouse setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}	
	
	public Integer getDepartment() {
		return department;
	}
	public Warehouse setDepartment(Integer department) {
		this.department = department;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Warehouse setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Warehouse setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Warehouse setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public Warehouse setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public boolean isEmpty() {
	    return getId() == null && getName() == null
	            && getDepartment() == null && getDomain() == null
	            && getWorkplace() == null;
	            
    }
}
