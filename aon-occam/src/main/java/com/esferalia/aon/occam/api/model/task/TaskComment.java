package com.esferalia.aon.occam.api.model.task;

import java.util.Date;

import com.esferalia.aon.occam.api.model.security.User;

public class TaskComment {
	String comment;
	Date createDate;
	Integer domain;
	Integer id;
	User user;
	Integer task;
	Date updateDate;
	
	public TaskComment() {}

	public String getComment() {
		return comment;
	}

	public TaskComment setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public TaskComment setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public User getUser() {
		return user;
	}

	public TaskComment setUser(User user) {
		this.user = user;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskComment setTask(Integer task) {
		this.task = task;
		return this;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public TaskComment setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
		return this;
	}
	
	
}
