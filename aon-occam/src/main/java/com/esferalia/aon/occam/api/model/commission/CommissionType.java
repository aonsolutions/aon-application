package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public class CommissionType implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private int domain;
	private String name;
	private double rate;
	
	public Integer getId() {
		return id;
	}
	public CommissionType setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public CommissionType setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public CommissionType setName(String name) {
		this.name = name;
		return this;
	}
	public double getRate() {
		return rate;
	}
	public CommissionType setRate(double rate) {
		this.rate = rate;
		return this;
	}
	
}
