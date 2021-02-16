package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

import com.sun.tools.javac.code.Attribute.Array;


public class Notification {

	private Integer id;
	private String title;
	private String body;
	private String path_image;
	private String url;
	private String device_token;
	private JSONObject data;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getUrl() {
		return url;
	}

	public String getDeviceToken() {
		return device_token;
	}
	public JSONObject getData() {
		return data;
	}
	
	public String getPathImage() {
		return path_image;
	}
	
	public Notification setPathImage(String path_image) {
		this.path_image = path_image;
		return this;
	}
	
	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}
	public Notification setBody(String body) {
		this.body = body;
		return this;
	}
	public Notification setDeviceToken(String device_token) {
		this.device_token = device_token;
		return this;
	}
	public Notification setUrl(String url) {
		this.url = url;
		return this;
	}
	public Notification setData(JSONObject data) {
		this.data = data;
		return this;
	}
	

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("title", getTitle());
		json.put("body", getBody());
		json.put("url", getUrl());
		json.put("image", getPathImage());
		json.put("data", getData());
		json.put("device_token", getDeviceToken());
		return json;
	}
}
