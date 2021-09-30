package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public class ProjectType  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String description;
	private boolean active;
	private boolean dirty;
	
	public Integer getId() {
		return id;
	}

	public ProjectType setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ProjectType setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ProjectType setDescription(String description) {
		setDirty(true);
		this.description = description;
		return this;
	}

	public boolean isActive() {
		return active;
	}
	
	public ProjectType setActive(boolean active) {
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
	
	public ProjectType setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
}
