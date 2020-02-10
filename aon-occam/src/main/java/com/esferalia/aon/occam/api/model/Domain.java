package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.DomainType;

public class Domain implements Serializable {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private Integer parentId;
	private DomainType domainType;
	private boolean enableHeredity;
	private boolean domainManagement;
	private boolean active;
	private Integer scope;

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
		return parentId == null && isDomainManagement();
	}
	public boolean isChild() {
		return parentId != null;
	}
	public boolean isStandalone() {
		return parentId == null && !isDomainManagement();
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

	public boolean isDomainManagement() {
		return domainManagement;
	}

	public Domain setDomainManagement(boolean domainManagement) {
		this.domainManagement = domainManagement;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

}
