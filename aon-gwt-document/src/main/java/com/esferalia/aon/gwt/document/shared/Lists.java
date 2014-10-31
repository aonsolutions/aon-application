package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Lists implements IsSerializable{

	CategoryList categoryList = new CategoryList();
	TagList tagList = new TagList();
	ScopeList scopeList = new ScopeList();
	
	public Lists() {

	}
	
	public CategoryList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(CategoryList categoryList) {
		this.categoryList = categoryList;
	}
	public TagList getTagList() {
		return tagList;
	}
	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	public ScopeList getScopeList() {
		return scopeList;
	}
	public void setScopeList(ScopeList scopeList) {
		this.scopeList = scopeList;
	}
}
