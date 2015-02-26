package com.esferalia.aon.occam.api.model.registry;

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
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	
}
