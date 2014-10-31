package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchInfo implements IsSerializable{
	private String name;
	private Boolean confidential;
	private String date;
	private String category;
	private Vector<String> tag;
	private String scope;
	private Vector<String> yoTag;
	
 	public SearchInfo() {

	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getConfidential() {
		return confidential;
	}
	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Vector<String> getTag() {
		return tag;
	}
	public void setTag(Vector<String> tag) {
		this.tag = tag;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public Vector<String> getYoTag() {
		return yoTag;
	}

	public void setYoTag(Vector<String> yoTag) {
		this.yoTag = yoTag;
	}
	

	
}
