package com.esferalia.aon.occam.api.model.product;

import java.util.Date;

public class ItemAddInfo {

	private Integer id;
	private Integer domain;
	private Integer product;
	private Integer item;
	private String attribute;
	private String value;
	private Date date;
	
	public ItemAddInfo() {
	
	}

	public Integer getId() {
		return id;
	}

	public ItemAddInfo setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ItemAddInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getProduct() {
		return product;
	}

	public ItemAddInfo setProduct(Integer product) {
		this.product = product;
		return this;
	}

	public Integer getItem() {
		return item;
	}

	public ItemAddInfo setItem(Integer item) {
		this.item = item;
		return this;
	}

	public String getAttribute() {
		return attribute;
	}

	public ItemAddInfo setAttribute(String attribute) {
		this.attribute = attribute;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ItemAddInfo setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public ItemAddInfo setDate(Date date) {
		this.date = date;
		return this;
	}
	
}
