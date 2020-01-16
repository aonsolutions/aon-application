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
	private Boolean active;
	
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

	public Boolean getActive() {
		return active;
	}

	public ProjectType setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
	

}
