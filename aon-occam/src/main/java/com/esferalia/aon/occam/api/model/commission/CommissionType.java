package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public class CommissionType implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Double rate;
	
	public Integer getId() {
		return id;
	}
	public CommissionType setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CommissionType setDomain(Integer domain) {
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
	public Double getRate() {
		return rate;
	}
	public CommissionType setRate(Double rate) {
		this.rate = rate;
		return this;
	}
	
	public Boolean isEmpty() {
		return getId() == null
			&& getDomain() == null
			&& getName() == null
			&& getRate() == null;
	}
	
}
