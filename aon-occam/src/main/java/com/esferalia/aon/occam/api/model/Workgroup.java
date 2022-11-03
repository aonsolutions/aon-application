package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;

public class Workgroup implements Serializable {
	
	private static final long serialVersionUID = -6673378118872331822L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private WorkgroupStatus status;
	private boolean dirty;
	
	private boolean removed;

	public WorkgroupStatus getStatus() {
		return status;
	}
	
	public Workgroup setStatus(WorkgroupStatus status) {
		this.status = status;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Workgroup setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Workgroup setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Workgroup setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public Workgroup setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public Workgroup setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public boolean isActive(){
		return WorkgroupStatus.ACTIVE == getStatus();
	}
	
	public Workgroup setActive(boolean active) {
		setStatus(active ? WorkgroupStatus.ACTIVE : WorkgroupStatus.INACTIVE);
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
			&& getDescription() == null;
	}
}
