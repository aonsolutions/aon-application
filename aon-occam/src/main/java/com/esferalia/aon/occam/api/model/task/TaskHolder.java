package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.Registry;

@SuppressWarnings("serial")
public class TaskHolder extends Registry implements Serializable{
	
	private TaskHolderType type;
	private Boolean active;
	private Integer userId;
	private Integer costProfile;
	
	public TaskHolder() {}
	
	public TaskHolder copy(Registry registry) {
		return super.copy( registry, this);
	}
	
	public TaskHolderType getType() {
		return type;
	}

	public TaskHolder setType(TaskHolderType type) {
		this.type = type;
		return this;
	}

	public byte getActiveValue() {
		return isActive() ? (byte) 1 : (byte) 0; 
	}
	
	public Boolean isActive() {
		return active;
	}

	public TaskHolder setActive(Boolean active) {
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
	
	public boolean isEmpty() {
		return super.isEmpty() && getType() == null
			&& isActive() == null && getUserId() == null 
			&& getCostProfile() == null;
	}
}
