package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TaskHolder implements Serializable{

	private Integer id; // registry
	private Integer domain;
	private Byte type;
	private Byte active;
	private Integer userId;
	private Integer costProfile;
	
	public TaskHolder() {}

	public Integer getId() {
		return id;
	}

	public TaskHolder setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TaskHolder setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Byte getType() {
		return type;
	}

	public TaskHolder setType(Byte type) {
		this.type = type;
		return this;
	}

	public Byte getActive() {
		return active;
	}

	public TaskHolder setActive(Byte active) {
		this.active = active;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public TaskHolder setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getCostProfile() {
		return costProfile;
	}

	public TaskHolder setCostProfile(Integer costProfile) {
		this.costProfile = costProfile;
		return this;
	}
}
