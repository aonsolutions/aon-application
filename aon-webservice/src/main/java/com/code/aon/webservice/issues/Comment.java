package com.code.aon.webservice.issues;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Comment {
	
	private Integer id;
	private String url;
	private String body;
	private User user;
	private String createdAt;
	private String updatedAt;
	
	public Comment() {
	
	}

	public Comment(TaskComment taskComment) {
		this.id = taskComment.getId();
		this.url = "";
		this.body = taskComment.getComment();
		this.user = new User(taskComment.getCreationUser());
		this.createdAt = AonDateUtils.dateTimeFormat(taskComment.getCreationDate());
		this.updatedAt = AonDateUtils.dateTimeFormat(taskComment.getModificationDate());
	}	
	
	public Integer getId() {
		return id;
	}
	
	public Comment setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Comment setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getBody() {
		return body;
	}
	
	public Comment setBody(String body) {
		this.body = body;
		return this;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	
	public Comment setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
		return this;
	}
	
	public String getUpdatedAt() {
		return updatedAt;
	}
	
	public Comment setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public Comment setUser(User user) {
		this.user = user;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("url", getUrl());
		json.put("body", getBody());
		json.put("created_at", getCreatedAt());
		json.put("updated_at", getUpdatedAt());
		json.put("user", getUser().toJSON());
				
		return json;
	}	
}
