package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class SellerWorkloadParams extends SellerParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Byte customers;
	private Byte period;
	private Integer seller;
	private Integer taskHolder;
	
	public SellerWorkloadParams() {
		super();
	}

	public Byte getCustomers() {
		return customers;
	}

	public SellerWorkloadParams setCustomers(Byte customers) {
		this.customers = customers;
		return this;
	}
	
	public Byte getPeriod() {
		return period;
	}

	public SellerWorkloadParams setPeriod(Byte period) {
		this.period = period;
		return this;
	}
	
	public Integer getSeller() {
		return seller;
	}

	public SellerWorkloadParams setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	
	public Integer getTaskHolder() {
		return taskHolder;
	}

	public SellerWorkloadParams setTaskHolder(Integer taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
}
