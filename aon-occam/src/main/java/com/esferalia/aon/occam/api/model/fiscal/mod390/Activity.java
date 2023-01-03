package com.esferalia.aon.occam.api.model.fiscal.mod390;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.ActivityType;

public class Activity implements Serializable {

	private static final long serialVersionUID = -1202033752419394011L;
	
	private String description;
	private String key;	
	private ActivityType type;
	private String epigraph;
	
	public String getDescription() {
		return description;
	}
	public Activity setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getKey() {
		return key;
	}
	public Activity setKey(String key) {
		this.key = key;
		return this;
	}
	public ActivityType getType() {
		return type;
	}
	public Activity setType(ActivityType type) {
		this.type = type;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public Activity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
}
