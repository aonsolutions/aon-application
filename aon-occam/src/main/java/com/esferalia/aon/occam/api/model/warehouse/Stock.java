package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Stock implements Serializable {
	
	private Integer domain;
	private Integer id;
	private Integer item;
	private Double quantity;
	private Integer warehouse;

	public Integer getDomain() {
		return domain;
	}
	public Stock setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public Stock setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getItem() {
		return item;
	}
	public Stock setItem(Integer item) {
		this.item = item;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public Stock setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public Stock setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
}

