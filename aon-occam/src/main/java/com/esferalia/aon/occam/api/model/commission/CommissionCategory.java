package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public class CommissionCategory implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private Integer commission;
	private Integer category;
	private Double quantity;
	private Double rate;
	
	public Integer getId() {
		return id;
	}
	public CommissionCategory setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CommissionCategory setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Double getRate() {
		return rate;
	}
	public CommissionCategory setRate(Double rate) {
		this.rate = rate;
		return this;
	}
	public Integer getCommission() {
		return commission;
	}
	public CommissionCategory setCommission(Integer commission) {
		this.commission = commission;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public CommissionCategory setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Integer getCategory() {
		return category;
	}
	public CommissionCategory setCategory(Integer category) {
		this.category = category;
		return this;
	}
	
}
