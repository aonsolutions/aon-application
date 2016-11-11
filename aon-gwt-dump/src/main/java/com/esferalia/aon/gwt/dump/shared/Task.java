package com.esferalia.aon.gwt.dump.shared;

import java.io.Serializable;

public class Task implements Serializable {

	private Integer id;
	private String description;
	private Integer idRattach;

	public Integer getId() {
		return id;
	}
	
	public Task setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}
}
