package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;

public class JobType  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain; 
	private String description; 

	
	public JobType() { 
	}

	public Integer getId() {
		return id;
	}

	public JobType setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		if(domain == null) {
			domain = new Domain();
		}
		return domain;
	}

	public JobType setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public JobType setDescription(String description) {
		this.description = description;
		return this;
	}
}
