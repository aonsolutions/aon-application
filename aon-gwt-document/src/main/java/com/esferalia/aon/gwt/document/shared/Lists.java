package com.esferalia.aon.gwt.document.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Lists implements IsSerializable{

	CategoryList categoryList = new CategoryList();
	CategoryList categoryListSon = new CategoryList();
	TagList tagList = new TagList();
	TagList tagListSon = new TagList();
	ScopeList scopeList = new ScopeList();
	ScopeList scopeListSon = new ScopeList();
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

	public CategoryList getCategoryListSon() {
		return categoryListSon;
	}

	public void setCategoryListSon(CategoryList categoryListSon) {
		this.categoryListSon = categoryListSon;
	}

	public TagList getTagListSon() {
		return tagListSon;
	}

	public void setTagListSon(TagList tagListSon) {
		this.tagListSon = tagListSon;
	}

	public ScopeList getScopeListSon() {
		return scopeListSon;
	}

	public void setScopeListSon(ScopeList scopeListSon) {
		this.scopeListSon = scopeListSon;
	}
	
}
