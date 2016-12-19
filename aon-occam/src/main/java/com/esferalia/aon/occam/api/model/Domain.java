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
	Byte domainManagement;
	
	boolean parent;
	boolean child;
	boolean standalone;
	boolean enableHeredity;
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

	public Domain setParentId(Integer parentId) {
		this.parentId = parentId;
		return this;
	}	

	public boolean isParent() {
		return parent;
	}

	public Domain setParent(boolean parent) {
		this.parent = parent;
		return this;
	}

	public boolean isChild() {
		return child;
	}

	public Domain setChild(boolean child) {
		this.child = child;
		return this;
	}

	public boolean isStandalone() {
		return standalone;
	}

	public Domain setStandalone(boolean standalone) {
		this.standalone = standalone;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Domain setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Domain setDescription(String description) {
		this.description = description;
		return this;
	}

	public DomainType getDomainType() {
		return domainType;
	}

	public Domain setDomainType(DomainType domainType) {
		this.domainType = domainType;
		return this;
	}

	public boolean isEnableHeredity() {
		return enableHeredity;
	}

	public Domain setEnableHeredity(boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
		return this;
	}

	public Byte getDomainManagement() {
		return domainManagement;
	}

	public Domain setDomainManagement(Byte domainManagement) {
		this.domainManagement = domainManagement;
		return this;
	}
	
	

}
