package com.code.aon.webservice.issues;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Event {
	
	private Integer id;
	private String url;
	private String event;
	private String createdAt;
	private User user;
	
	public Event() {
	
	}
	
	public Event(TaskEvent taskEvent) {
		this.id = taskEvent.getId();
		this.url = "";
		this.event = taskEvent.getEvent();
		this.createdAt = AonDateUtils.dateTimeFormat(taskEvent.getCreationDate());
		this.user = new User(taskEvent.getCreationUser());
	}
	
	public Integer getId() {
		return id;
	}
	
	public Event setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Event setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}
	
	public Event setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public Event setUser(User user) {
		this.user = user;
		return this;
	}
	
	public String getEvent() {
		return event;
	}

	public Event setEvent(String event) {
		this.event = event;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("url", getUrl());
		json.put("created_at", getCreatedAt());
		json.put("actor", getUser().toJSON());
		json.put("event", getEvent());	
		return json;
	}	
}
