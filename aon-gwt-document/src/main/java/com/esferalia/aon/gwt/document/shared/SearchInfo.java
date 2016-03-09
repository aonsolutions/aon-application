package com.esferalia.aon.gwt.document.shared;

import java.util.LinkedList;
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
	
	private String domain;
	
	private LinkedList<String> categoryList;
	private LinkedList<String> tagList;
	
 	public SearchInfo() {

	}
	
	public String getName() {
		return name;
	}
	public SearchInfo setName(String name) {
		this.name = name;
		return this;
	}
	public Boolean getConfidential() {
		return confidential;
	}
	public SearchInfo setConfidential(Boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	public String getDate() {
		return date;
	}
	public SearchInfo setDate(String date) {
		this.date = date;
		return this;
	}
	public String getCategory() {
		return category;
	}
	public SearchInfo setCategory(String category) {
		this.category = category;
		return this;
	}
	public Vector<String> getTag() {
		return tag;
	}
	public SearchInfo setTag(Vector<String> tag) {
		this.tag = tag;
		return this;
	}
	public String getScope() {
		return scope;
	}
	public SearchInfo setScope(String scope) {
		this.scope = scope;
		return this;
	}

	public Vector<String> getYoTag() {
		return yoTag;
	}

	public SearchInfo setYoTag(Vector<String> yoTag) {
		this.yoTag = yoTag;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public SearchInfo setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public LinkedList<String> getCategoryList() {
		return categoryList;
	}

	public SearchInfo setCategoryList(LinkedList<String> categoryList) {
		this.categoryList = categoryList;
		return this;
	}

	public LinkedList<String> getTagList() {
		return tagList;
	}

	public SearchInfo setTagList(LinkedList<String> tagList) {
		this.tagList = tagList;
		return this;
	}
	
	
	
}
