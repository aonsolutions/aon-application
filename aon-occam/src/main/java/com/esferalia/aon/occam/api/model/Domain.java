package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.DomainType;

@SuppressWarnings("serial")
public class Domain implements Serializable {

	Integer id;
	String name;
	Integer parentId;
	String description;
	DomainType domainType;
	
	boolean parent;
	boolean child;
	boolean standalone;
	
	boolean active;

	public Integer getId() {
		return id;
	}

	public Domain setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Domain setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public boolean isChild() {
		return child;
	}

	public void setChild(boolean child) {
		this.child = child;
	}

	public boolean isStandalone() {
		return standalone;
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainType getDomainType() {
		return domainType;
	}

	public void setDomainType(DomainType domainType) {
		this.domainType = domainType;
	}
	
	

}
