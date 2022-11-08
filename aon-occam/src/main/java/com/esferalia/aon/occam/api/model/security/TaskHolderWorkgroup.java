package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Workgroup;

@SuppressWarnings("serial")
public class TaskHolderWorkgroup implements Serializable {
	
	Integer id;
	Integer domain;
	Integer taskHolder;
	Workgroup workgroup;
	
	private boolean removed;

	public Integer getDomain() {
		return domain;
	}

	public TaskHolderWorkgroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public TaskHolderWorkgroup setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getTaskHolder() {
		return taskHolder;
	}

	public TaskHolderWorkgroup setTaskHolder(Integer taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public Workgroup getWorkgroup() {
		if(workgroup == null) {
			workgroup = new Workgroup();
		}
		return workgroup;
	}

	public TaskHolderWorkgroup setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public TaskHolderWorkgroup setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
			&& getTaskHolder() == null && getWorkgroup().isEmpty();
	}
	
}
