package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.CategoryType;

@SuppressWarnings("serial")
public class Category implements Serializable{
	
	String description;
	Integer domain;
	Integer id;
	Integer rattach;
	String name;
	Integer scope;
	Byte type;
	String url;
	
	private CategoryType categoryType;
	
	public String getDescription() {
		return description;
	}
	
	public Category setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Category setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Category setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getRattach() {
		return rattach;
	}
	
	public Category setRattach(Integer rattach) {
		this.rattach = rattach;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Category setName(String name) {
		this.name = name;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	
	public Category setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public CategoryType getCategoryType() {
		return categoryType;
	}
	
	public Category setCategoryType(CategoryType categoryType) {
		if(categoryType!=null) {
			this.type = categoryType.value();
			this.categoryType = categoryType;
		}
		return this;
	}
	
	public Byte getType() {
		return type;
	}
	
	public Category setType(Byte type) {
		this.type = type;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	public Category setUrl(String url) {
		this.url = url;
		return this;
	}
}
