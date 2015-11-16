package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public class ProductCategory implements Serializable {
	
	private static final long serialVersionUID = -9167695204686231161L;
	
	Integer id;
	Integer domain;
	String name;
	boolean selected;
	
	public Integer getId() {
		return id;
	}
	public ProductCategory setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ProductCategory setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public ProductCategory setName(String name) {
		this.name = name;
		return this;
	}
	public boolean isSelected() {
		return selected;
	}
	public ProductCategory setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
}
