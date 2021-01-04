package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Date;
import java.util.LinkedList;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class TimeControl {
	
	private Date startDate;
	private TimeControlGroup group;
	
	private Long time;
	private TimeControlStatus status;
	private TaskHolder taskHolder;
	private Date inDate;
	private LinkedList<TimeControlDetail> detail;
	
	public TimeControl() {
	
	}
	
	public Long getTime() {
		return time;
	}

	public TimeControl setTime(Long time) {
		this.time = time;
		return this;
	}

	public TimeControlStatus getStatus() {
		return status;
	}

	public TimeControl setStatus(TimeControlStatus status) {
		this.status = status;
		return this;
	}

	public Date getInDate() {
		return inDate;
	}

	public TimeControl setInDate(Date inDate) {
		this.inDate = inDate;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public TimeControl setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
	public LinkedList<TimeControlDetail> getDetail() {
		return detail;
	}

	public TimeControl setDetail(LinkedList<TimeControlDetail> detail) {
		this.detail = detail;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("time", getTime());
		json.put("in_date", getInDate().getTime());
		json.put("status", getStatus().name().toLowerCase());
		
		JSONObject taskHolderJson = new JSONObject();
		taskHolderJson.put("id", getTaskHolder().getId());
		taskHolderJson.put("name", getTaskHolder().getName());

		json.put("task_holder", taskHolderJson);
		return json;
	}
}
