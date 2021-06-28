package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONObject;

@SuppressWarnings("serial")
@Deprecated
public class OldTask  implements Serializable{
	
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
	private Integer sourceId;
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
	
	
	
	public OldTask() {
		
	}

	public Integer getId() {
		return id;
	}

	public OldTask setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public OldTask setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public OldTask setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public OldTask setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public OldTask setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public OldTask setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Byte getPriority() {
		return priority;
	}

	public OldTask setPriority(Byte priority) {
		this.priority = priority;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public OldTask setStatus(Byte status) {
		this.status = status;
		return this;
	}

	public Byte getPercent() {
		return percent;
	}

	public OldTask setPercent(Byte percent) {
		this.percent = percent;
		return this;
	}

	public Integer getTaskHolder() {
		return taskHolder;
	}

	public OldTask setTaskHolder(Integer taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public Integer getWorkgroup() {
		return workgroup;
	}

	public OldTask setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public Byte getSource() {
		return source;
	}

	public OldTask setSource(Byte source) {
		this.source = source;
		return this;
	}
	
	public Integer getSourceId() {
		return sourceId;
	}

	public OldTask setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public Integer getProject() {
		return project;
	}

	public OldTask setProject(Integer project) {
		this.project = project;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public OldTask setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public OldTask setActivityType(Integer activityType) {
		this.activityType = activityType;
		return this;
	}

	public Integer getSender() {
		return sender;
	}

	public OldTask setSender(Integer sender) {
		this.sender = sender;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public OldTask setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Byte getRepeatPeriod() {
		return repeatPeriod;
	}

	public OldTask setRepeatPeriod(Byte repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
		return this;
	}

	public String getGtaskId() {
		return gtaskId;
	}

	public OldTask setGtaskId(String gtaskId) {
		this.gtaskId = gtaskId;
		return this;
	}

	public String getGtasklistId() {
		return gtasklistId;
	}

	public OldTask setGtasklistId(String gtasklistId) {
		this.gtasklistId = gtasklistId;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}

	public OldTask setNumber(Integer number) {
		this.number = number;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}

	public OldTask setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public OldTask setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}

	public OldTask setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public OldTask setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Integer getParent() {
		return parent;
	}

	public OldTask setParent(Integer parent) {
		this.parent = parent;
		return this;
	}
	
	public Timestamp toTimestamp(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain());
		json.put("description", getDescription());
		json.put("priority", getPriority());
		json.put("status", getStatus());
		json.put("percent", getPercent());
		json.put("taskHolder", getTaskHolder());
		json.put("workgroup", getWorkgroup());
		json.put("source", getSource());
		json.put("sourceId", getSourceId());
		json.put("project", getProject());
		json.put("registry", getRegistry());
		json.put("activityType", getActivityType());
		json.put("sender", getSender());
		json.put("comments", getComments());
		json.put("repeatPeriod", getRepeatPeriod());
		json.put("gtaskId", getGtaskId());
		json.put("gtasklistId", getGtasklistId());
		json.put("number", getNumber());
		json.put("creationUser", getCreationUser());
		json.put("number", getNumber());
		json.put("modificationUser", getModificationUser());
		json.put("parent", getParent());
		json.put("creationDate", getCreationDate()!=null ? getCreationDate().getTime() : null);
		json.put("modificationDate", getModificationDate()!=null ? getModificationDate().getTime() : null);
		json.put("startDate", getStartDate()!=null ? getStartDate().getTime() : null);
		json.put("endDate",  getEndDate()!=null ? getEndDate().getTime() : null);
		json.put("dueDate", getDueDate()!=null ? getDueDate().getTime(): null);
		return json;
	}

}
