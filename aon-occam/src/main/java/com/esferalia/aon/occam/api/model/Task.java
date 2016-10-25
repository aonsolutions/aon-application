package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("serial")
public class Task  implements Serializable{
	
	private Integer id;
	private Integer domain; 
	private String description; 
	private Date startDate; 
	private Date endDate; 
	private Date dueDate; 
	private Byte priority; 
	private Byte status; 
	private Byte percent; 
	private Integer taskHolder; 
	private Integer workgroup; 
	private Byte source; 
	private Integer project; 
	private Integer registry; 
	private Integer activityType; 
	private Integer sender; 
	private String comments; 
	private Byte repeatPeriod; 
	private String gtaskId; 
	private String gtasklistId; 
	
	private Integer number;
	private Integer parent;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	
	
	public Task() {
		
	}

	public Integer getId() {
		return id;
	}

	public Task setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Task setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Task setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Task setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public Task setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Byte getPriority() {
		return priority;
	}

	public Task setPriority(Byte priority) {
		this.priority = priority;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public Task setStatus(Byte status) {
		this.status = status;
		return this;
	}

	public Byte getPercent() {
		return percent;
	}

	public Task setPercent(Byte percent) {
		this.percent = percent;
		return this;
	}

	public Integer getTaskHolder() {
		return taskHolder;
	}

	public Task setTaskHolder(Integer taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public Integer getWorkgroup() {
		return workgroup;
	}

	public Task setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public Byte getSource() {
		return source;
	}

	public Task setSource(Byte source) {
		this.source = source;
		return this;
	}

	public Integer getProject() {
		return project;
	}

	public Task setProject(Integer project) {
		this.project = project;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Task setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public Task setActivityType(Integer activityType) {
		this.activityType = activityType;
		return this;
	}

	public Integer getSender() {
		return sender;
	}

	public Task setSender(Integer sender) {
		this.sender = sender;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Task setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Byte getRepeatPeriod() {
		return repeatPeriod;
	}

	public Task setRepeatPeriod(Byte repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
		return this;
	}

	public String getGtaskId() {
		return gtaskId;
	}

	public Task setGtaskId(String gtaskId) {
		this.gtaskId = gtaskId;
		return this;
	}

	public String getGtasklistId() {
		return gtasklistId;
	}

	public Task setGtasklistId(String gtasklistId) {
		this.gtasklistId = gtasklistId;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}

	public Task setNumber(Integer number) {
		this.number = number;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}

	public Task setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public Task setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}

	public Task setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public Task setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Integer getParent() {
		return parent;
	}

	public Task setParent(Integer parent) {
		this.parent = parent;
		return this;
	}
	
	public Timestamp toTimestamp(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}
}
