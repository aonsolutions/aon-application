package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public class ProductCategory implements Serializable {
	
	private static final long serialVersionUID = -9167695204686231161L;
	
	Integer id;
	Integer domain;
	String name;
	
	String detail;
	String detail2;
	String detail3;
	
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
	public String getDetail() {
		return detail;
	}
	public ProductCategory setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public ProductCategory setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public ProductCategory setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	
	public Boolean isEmpty() {
		return getId() == null && getDomain() == null && getName() == null
			&& getDetail() == null && getDetail2() == null && getDetail3() == null;
	}
	
}
