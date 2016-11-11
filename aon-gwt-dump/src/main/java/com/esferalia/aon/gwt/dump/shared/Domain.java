package com.esferalia.aon.gwt.dump.shared;

import java.io.Serializable;

public class Domain implements Serializable {

	private Integer id;
	private String name;
	private String description;
	private String suffix;

	public String getSuffix() {
		return suffix;
	}
	public Domain setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
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
	public String getDescription() {
		return description;
	}
	public Domain setDescription(String description) {
		this.description = description;
		return this;
	}
	
	
}
