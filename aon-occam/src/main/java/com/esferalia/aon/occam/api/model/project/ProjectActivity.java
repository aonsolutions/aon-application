package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.ActivityType;

public class ProjectActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer project;
	private ActivityType activityType;
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
	
	public ActivityType getActivityType() {
		if(activityType==null)
			activityType = new ActivityType();
		return activityType;
	}

	public ProjectActivity setActivityType(ActivityType activityType) {
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
		setDirty(true);
		this.removed = removed;
		return this;
	}
}
