package com.esferalia.aon.gwt.document.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Lists implements IsSerializable{
	LinkedList<Category> categoryListDomainZero = new LinkedList<Category>();
	LinkedList<Category> categoryList = new LinkedList<Category>();
	LinkedList<Category> categoryListSon = new LinkedList<Category>();
	TagList tagListDomainZero = new TagList();
	TagList tagList = new TagList();
	TagList tagListSon = new TagList();
	ScopeList scopeList = new ScopeList();
	ScopeList scopeListSon = new ScopeList();
	public Lists() {
		
	}
	
	public LinkedList<Category> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(LinkedList<Category> categoryList) {
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

	public LinkedList<Category> getCategoryListSon() {
		return categoryListSon;
	}

	public void setCategoryListSon(LinkedList<Category> categoryListSon) {
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

	public LinkedList<Category> getCategoryListDomainZero() {
		return categoryListDomainZero;
	}

	public void setCategoryListDomainZero(LinkedList<Category> categoryListDomainZero) {
		this.categoryListDomainZero = categoryListDomainZero;
	}

	public TagList getTagListDomainZero() {
		return tagListDomainZero;
	}

	public void setTagListDomainZero(TagList tagListDomainZero) {
		this.tagListDomainZero = tagListDomainZero;
	}
	
}
