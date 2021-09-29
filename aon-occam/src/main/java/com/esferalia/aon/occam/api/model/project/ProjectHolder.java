package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class ProjectHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer project;
	private Date startDate;
	private Date endDate;
	private Workgroup workgroup;
	private TaskHolder taskHolder;
	
	public ProjectHolder() {
		// Nothing to do.
	}

	public Integer getId() {
		return id;
	}

	public ProjectHolder setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ProjectHolder setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getProject() {
		return project;
	}

	public ProjectHolder setProject(Integer project) {
		this.project = project;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ProjectHolder setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ProjectHolder setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Workgroup getWorkgroup() {
		if(workgroup == null) {
			workgroup = new Workgroup();
		}
		return workgroup;
	}

	public ProjectHolder setWorkgroup(Workgroup workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public TaskHolder getTaskHolder() {
		if(taskHolder == null) {
			taskHolder = new TaskHolder();
		}
		return taskHolder;
	}

	public ProjectHolder setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getEndDate() == null
				&& getStartDate() == null && getTaskHolder().isEmpty()
				&& getWorkgroup().isEmpty();
				//|| getProject().isEmpty() 
	}
	
}
