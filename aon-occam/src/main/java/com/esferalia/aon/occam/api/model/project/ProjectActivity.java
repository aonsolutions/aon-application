package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public class ProjectActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer project;
	private Integer activityType;
	private boolean active;
	private boolean dirty;
	private boolean removed;
	
	public ProjectActivity() {
		// Nothing to do.
	}

	public Integer getId() {
		return id;
	}

	public ProjectActivity setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ProjectActivity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getProject() {
		return project;
	}

	public ProjectActivity setProject(Integer project) {
		this.project = project;
		return this;
	}
	
	public Integer getActivityType() {
		return activityType;
	}

	public ProjectActivity setActivityType(Integer activityType) {
		setDirty(true);
		this.activityType = activityType;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}

	public ProjectActivity setActive(boolean active) {
		setDirty(true);
		this.active = active;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public ProjectActivity setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public ProjectActivity setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
}
