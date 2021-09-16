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
	
	public ProjectType() {
	
	}

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
		this.description = description;
		return this;
	}

	public boolean getActive() {
		return active;
	}

	public ProjectType setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
			&& getDescription() == null;
	}

}
