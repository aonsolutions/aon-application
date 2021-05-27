package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("serial")
public class TaskEvent implements Serializable{

	private String event;
	private Integer domain;
	private Integer id;
	private Integer task;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public TaskEvent() {}

	public String getEvent() {
		return event;
	}

	public TaskEvent setEvent(String event) {
		this.event = event;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TaskEvent setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public TaskEvent setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskEvent setTask(Integer task) {
		this.task = task;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}

	public TaskEvent setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public TaskEvent setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public TaskEvent setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public TaskEvent setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public Timestamp toTimestamp(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}
	
}
