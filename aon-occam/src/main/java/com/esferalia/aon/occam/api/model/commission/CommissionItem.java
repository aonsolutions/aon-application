package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public class CommissionItem implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private Integer commission;
	private Integer item;
	private Double quantity;
	private Double amount;
	private Double rate;
	
	public Integer getId() {
		return id;
	}
	public CommissionItem setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CommissionItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Double getRate() {
		return rate;
	}
	public CommissionItem setRate(Double rate) {
		this.rate = rate;
		return this;
	}
	public Integer getCommission() {
		return commission;
	}
	public CommissionItem setCommission(Integer commission) {
		this.commission = commission;
		return this;
	}
	public Integer getItem() {
		return item;
	}
	public CommissionItem setItem(Integer item) {
		this.item = item;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public CommissionItem setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Double getAmount() {
		return amount;
	}
	public CommissionItem setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
}
