package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("serial")
public class TaskComment implements Serializable{
	
	private String comment;
	private Integer domain;
	private Integer id;
	private Integer task;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public TaskComment() {}

	public String getComment() {
		return comment;
	}

	public TaskComment setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TaskComment setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public TaskComment setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskComment setTask(Integer task) {
		this.task = task;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public TaskComment setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public TaskComment setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public TaskComment setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public TaskComment setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Timestamp toTimestamp(Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}
}
