package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class ActivityType  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String description;
	private Integer projectType;
	private boolean active;
	private boolean dirty;
	
	public Integer getId() {
		return id;
	}

	public ActivityType setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ActivityType setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ActivityType setDescription(String description) {
		setDirty(true);
		this.description = description;
		return this;
	}
	
	
	public Integer getProjectType() {
		return projectType;
	}

	public ActivityType setProjectType(Integer projectType) {
		setDirty(true);
		this.projectType = projectType;
		return this;
	}

	public boolean isActive() {
		return active;
	}
	
	public ActivityType setActive(boolean active) {
		setDirty(true);
		this.active = active;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
			&& getDescription() == null;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	public ActivityType setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
}
