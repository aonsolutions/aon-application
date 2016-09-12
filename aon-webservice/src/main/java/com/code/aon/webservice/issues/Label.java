package com.code.aon.webservice.issues;

import org.json.JSONObject;

public class Label {
	String url;
	String color;
	String name;

	public String getUrl() {
		return url;
	}
	public Label setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getColor() {
		return color;
	}
	public Label setColor(String color) {
		this.color = color;
		return this;
	}
	public String getName() {
		return name;
	}
	public Label setName(String name) {
		this.name = name;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("url", getUrl());
		json.put("color", getColor());
		json.put("name", getName());
		return json;
	}
}
