package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.occam.api.model.type.MimeType;

public class TaskAttach {

	private Integer id;
	private Integer domain;
	private Integer task;
	private Integer task_workflow;
	private MimeType mimetype;	
	private byte[] data;
	
	@Deprecated
	private Integer task_workgroup;

	public TaskAttach() {
	
	}

	public Integer getId() {
		return id;
	}

	public TaskAttach setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TaskAttach setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getTask() {
		return task;
	}

	public TaskAttach setTask(Integer task) {
		this.task = task;
		return this;
	}	
	
	public Integer getTaskWorkflow() {
		return task_workflow;
	}

	public TaskAttach setTaskWorkflow(Integer task_workflow) {
		this.task_workflow = task_workflow;
		return this;
	}
	

	public MimeType getMimetype() {
		return mimetype;
	}

	public TaskAttach setMimetype(MimeType mimetype) {
		this.mimetype = mimetype;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public TaskAttach setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	@Deprecated
	public Integer getTask_workgroup() {
		return task_workgroup;
	}
	@Deprecated
	public TaskAttach setTask_workgroup(Integer task_workflow) {
		this.task_workflow = task_workflow;
		return this;
	}
	
}
