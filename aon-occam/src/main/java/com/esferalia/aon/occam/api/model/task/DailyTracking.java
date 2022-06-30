package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;

public class DailyTracking  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain; 
	private TaskHolder taskHolder; 
	private Date trackingDate; 
	private Double trackingDuration; 
	private Integer jobType;
	private Registry registry; 
	private Project project; 
	private Integer activityType;
	private String comments; 
	private Integer task;
	private Double cost; 
	
	public DailyTracking() { 
	}

	public Integer getId() {
		return id;
	}

	public DailyTracking setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		if(domain == null) {
			domain = new Domain();
		}
		return domain;
	}

	public DailyTracking setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public TaskHolder getTaskHolder() {
		if(taskHolder == null) {
			taskHolder = new TaskHolder();
		}
		return taskHolder;
	}

	public DailyTracking setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
	public Date getTrackingDate() {
		return trackingDate;
	}

	public DailyTracking setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
		return this;
	}
	
	public Double getTrackingDuration() {
		return trackingDuration;
	}

	public DailyTracking setTrackingDuration(Double trackingDuration) {
		this.trackingDuration = trackingDuration;
		return this;
	}

	public Integer getJobType() {
		return jobType;
	}

	public DailyTracking setJobType(Integer jobType) {
		this.jobType = jobType;
		return this;
	}
	
	public Registry getRegistry() {
		return registry;
	}

	public DailyTracking setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	
	public Project getProject() {
		return project;
	}

	public DailyTracking setProject(Project project) {
		this.project = project;
		return this;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public DailyTracking setActivityType(Integer activityType) {
		this.activityType = activityType;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public DailyTracking setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Integer getTask() {
		return task;
	}

	public DailyTracking setTask(Integer task) {
		this.task = task;
		return this;
	}
	
	public Double getCost() {
		return cost;
	}

	public DailyTracking setCost(Double cost) {
		this.cost = cost;
		return this;
	}
	

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DailyTracking ) )
			return false;
		
		DailyTracking dailyTracking = (DailyTracking) obj;
		
		return Objects.equals(id, dailyTracking.id);
	}
}
