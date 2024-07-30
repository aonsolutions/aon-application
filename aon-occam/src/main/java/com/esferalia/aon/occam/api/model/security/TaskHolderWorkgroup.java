package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderWorkgroupType;

@SuppressWarnings("serial")
public class TaskHolderWorkgroup implements Serializable {
	
	Integer id;
	Integer domain;
	Integer taskHolder;
	TaskHolder taskholder;
	Workgroup workgroup;
	
	TaskHolderWorkgroupType taskHolderWorkgroupType;
	Date startDate;
	Date endDate;
	
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
	
	public TaskHolder getTaskHolderObj() {
		return taskholder;
	}

	public TaskHolderWorkgroup setTaskHolder(TaskHolder taskholder) {
		this.taskholder = taskholder;
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
	
	public TaskHolderWorkgroupType getTaskHolderWorkgroupType() {
		return taskHolderWorkgroupType;
	}

	public TaskHolderWorkgroup setTaskHolderWorkgroupType(TaskHolderWorkgroupType taskHolderWorkgroupType) {
		this.taskHolderWorkgroupType = taskHolderWorkgroupType;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public TaskHolderWorkgroup setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public TaskHolderWorkgroup setEndDate(Date endDate) {
		this.endDate = endDate;
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
