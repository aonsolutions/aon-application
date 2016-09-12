package com.esferalia.aon.occam.api.model.task;

import java.util.Date;

import com.esferalia.aon.occam.api.model.security.User;

public class TaskEvent {
	String event;
	Date createDate;
	Integer domain;
	Integer id;
	User user;
	Integer task;
	
	public TaskEvent() {}

	public String getEvent() {
		return event;
	}

	public TaskEvent setEvent(String event) {
		this.event = event;
		return this;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public TaskEvent setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public User getUser() {
		return user;
	}

	public TaskEvent setUser(User user) {
		this.user = user;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskEvent setTask(Integer task) {
		this.task = task;
		return this;
	}
	
	
}
