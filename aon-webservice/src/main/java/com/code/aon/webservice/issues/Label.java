package com.code.aon.webservice.issues;

import org.json.JSONObject;

public class Label {
	Integer id;
	String url;
	String color;
	String name;

	public Integer getId(){
		return id;
	}
	
	public Label setId(Integer id){
		this.id = id;
		return this;
	}
	
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
		json.put("id", getId());
		json.put("url", getUrl());
		json.put("color", getColor());
		json.put("name", getName());
		return json;
	}
}
