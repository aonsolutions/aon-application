package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class InvestAsset implements Serializable {

	private static final long serialVersionUID = 6972256004151447259L;
	
	private Integer id;
	private String description;
	private double percent;

	public Integer getId() {
		return id;
	}

	public InvestAsset setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public InvestAsset setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public InvestAsset setPercent(double percent) {
		this.percent = percent;
		return this;
	}

}
