package com.esferalia.aon.occam.api.model.task;

public class TaskTag {
	Integer id;
	Integer domain;
	Integer task;
	Integer tag;
	
	public TaskTag() {}

	public Integer getDomain() {
		return domain;
	}

	public TaskTag setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public TaskTag setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskTag setTask(Integer task) {
		this.task = task;
		return this;
	}
	
	public Integer getTag() {
		return tag;
	}

	public TaskTag setTag(Integer tag) {
		this.tag = tag;
		return this;
	}
	
	
}
