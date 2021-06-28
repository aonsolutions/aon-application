package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.occam.api.model.type.MimeType;

public class TaskAttach {

	private Integer id;
	private Integer domain;
	private Integer task;
	private Integer task_workgroup;
	private MimeType mimetype;	
	private byte[] data;
	
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

	public Integer getTask_workgroup() {
		return task_workgroup;
	}

	public TaskAttach setTask_workgroup(Integer task_workgroup) {
		this.task_workgroup = task_workgroup;
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
	
}
