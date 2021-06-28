package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class TaskWorkflow implements Serializable{
	
	private Integer id;
	private Integer domain;
	private Integer task;
	private TaskHolder taskHolder;
	private TaskWorkflowType type;
	private String comment;
	private LinkedList<TaskAttach> taskAttachList;

	// AUDITORIA
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public TaskWorkflow() {}

	public Integer getId() {
		return id;
	}

	public TaskWorkflow setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TaskWorkflow setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskWorkflow setTask(Integer task) {
		this.task = task;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public TaskWorkflow setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public TaskWorkflowType getType() {
		return type;
	}

	public TaskWorkflow setType(TaskWorkflowType type) {
		this.type = type;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public TaskWorkflow setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public LinkedList<TaskAttach> getTaskAttachList() {
		return taskAttachList;
	}

	public TaskWorkflow setTaskAttachList(LinkedList<TaskAttach> taskAttachList) {
		this.taskAttachList = taskAttachList;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public TaskWorkflow setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public TaskWorkflow setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public TaskWorkflow setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public TaskWorkflow setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
