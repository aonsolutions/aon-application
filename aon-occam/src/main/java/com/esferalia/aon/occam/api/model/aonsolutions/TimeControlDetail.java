package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class TimeControlDetail {
	private Integer id;
	private Domain domain;
	private TimeControlStatus status;
	private TaskHolder taskHolder;
	private Date date;
	private String comments;
	private Location location;
	private Coordinates coordinates;
	
	public TimeControlDetail() {
	
	}

	public Integer getId() {
		return id;
	}

	public TimeControlDetail setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}
	
	public TimeControlDetail setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public TimeControlStatus getStatus() {
		return status;
	}

	public TimeControlDetail setStatus(TimeControlStatus status) {
		this.status = status;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public TimeControlDetail setDate(Date date) {
		this.date = date;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public TimeControlDetail setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public TimeControlDetail setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Location getLocation() {
		return location;
	}

	public TimeControlDetail setLocation(Location location) {
		this.location = location;
		return this;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public TimeControlDetail setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain().getId());
		json.put("status", getStatus() != null ? getStatus().name().toLowerCase() : TimeControlStatus.OUT);
		json.put("status", getStatus() != null ? getStatus().name().toLowerCase() : TimeControlStatus.OUT);
		
		if(getCoordinates() != null) {
			json.put("coordinates", getCoordinates().toJSON());
		}

		
		if(getLocation() != null) {
			JSONObject locationJson = new JSONObject();
			locationJson.put("id", getLocation().getId());
			locationJson.put("name", getLocation().getDescription());
			json.put("location", locationJson);
		}
		
		if(getTaskHolder() != null) {
			JSONObject taskHolderJson = new JSONObject();
			taskHolderJson.put("id", getTaskHolder().getId());
			taskHolderJson.put("name", getTaskHolder().getName());
			json.put("task_holder", taskHolderJson);
		}
		return json;
	}
	
}
