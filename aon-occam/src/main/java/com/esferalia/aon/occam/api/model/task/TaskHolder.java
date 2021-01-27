package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.Registry;

@SuppressWarnings("serial")
public class TaskHolder extends Registry implements Serializable{
	
	private TaskHolderType taskHolderType;
	private Boolean active;
	private Integer userId;
	private Integer costProfile;
	
	public TaskHolder() {}
	
	public TaskHolder setRegistryData(Registry registry) {
		this.setId(registry.getId());
		this.setDomain(registry.getDomain());
		this.setDocument(registry.getDocument());
		this.setDocumentType(registry.getDocumentType());
		this.setDocumentCountry(registry.getDocumentCountry());
		this.setName(registry.getName());
		this.setAlias(registry.getAlias());
		this.setLegalPerson(registry.isLegalPerson());
		this.setNationality(registry.getNationality());
		this.setSecurityLevel(registry.getSecurityLevel());
		return this;
	}
	
	public TaskHolderType getTaskHolderType() {
		return taskHolderType;
	}

	public TaskHolder setTaskHolderType(TaskHolderType taskHolderType) {
		this.taskHolderType = taskHolderType;
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
}
