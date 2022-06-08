package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class TaskWorkflow implements Serializable{
	
	private Integer id;
	private Integer domain;
	private Integer task;
	private String email;
	private TaskHolder taskHolder;
	private TaskWorkflowType type;
	private String comment;
	private List<TaskAttach> taskAttachList;

	// AUDITORIA
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	private String notificationUser;
	private Date notificationDate;
	
	public TaskWorkflow() {
		// Do nothing.
	}

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
		if(taskHolder == null) {
			taskHolder = new TaskHolder();
		}	
		return taskHolder;	
	}

	public TaskWorkflow setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
	public String getEmail() {
		return email;
	}
	
	public TaskWorkflow setEmail(String email) {
		this.email = email;
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

	public List<TaskAttach> getTaskAttachList() {
		return taskAttachList;
	}

	public TaskWorkflow setTaskAttachList(List<TaskAttach> taskAttachList) {
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
	
	public String getNotificationUser() {
		return notificationUser;
	}
	
	public TaskWorkflow setNotificationUser(String notificationUser) {
		this.notificationUser = notificationUser;
		return this;
	}
	
	public Date getNotificationDate() {
		return notificationDate;
	}
	
	public TaskWorkflow setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
		return this;
	}
	
	public boolean isPublic() {
		return getNotificationDate() != null;
	}
	
	public boolean isPrivate() {
		return !isPublic();
	}
	
	public TaskWorkflow clone() {
		return new TaskWorkflow()
	       .setTask(task)
		   .setComment(comment)
		   .setTaskHolder(taskHolder)
		   .setEmail(email)
		   .setDomain(domain);
	}
}
