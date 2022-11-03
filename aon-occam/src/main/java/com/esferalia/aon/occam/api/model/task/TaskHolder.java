package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

@SuppressWarnings("serial")
public class TaskHolder extends Registry implements Serializable{
	
	private Integer registry;
	private TaskHolderType type;
	private Boolean active;
	private Integer userId;
	private Integer costProfile;
	
	private List<Workgroup> workgroups;
	
	
	public TaskHolder() {}
	
	public TaskHolder copy(Registry registry) {
		return super.copy( registry, this);
	}
	
	public Integer getRegistry() {
		return registry;
	}
	
	public TaskHolder setRegistry(Integer registry) {
		setId(registry);
		this.registry = registry;
		return this;
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
		return active != null && active;
	}

	public TaskHolder setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
	public TaskHolder setStatus(RegistryStatus status) {
		if(status!=null) {
			setActive(RegistryStatus.ACTIVE.equals(status));
		}
		return this;
	}
	
	public RegistryStatus getStatus() {
		return Boolean.TRUE.equals(isActive())  ? RegistryStatus.ACTIVE : RegistryStatus.INACTIVE;
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
	
	public List<Workgroup> getWorkgroups() {
		if(workgroups == null) {
			workgroups = new LinkedList<>();
		}
		return workgroups;
	}
	
	public TaskHolder setWorkgroups(List<Workgroup> workgroups) {
		this.workgroups = workgroups;
		return this;
	}
	
	public TaskHolder addWorkgroup(Workgroup workgroup) {
		getWorkgroups().add(workgroup);
		return this;
	}

	
	public boolean isEmpty() {
		return super.isEmpty() && getType() == null
			&& getUserId() == null && getCostProfile() == null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(registry);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TaskHolder ) )
			return false;
		
		TaskHolder th = (TaskHolder) obj;
		
		return Objects.equals(registry, th.registry);
	}
}
