package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class UteBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private double base;
	private double percent;

	public double getPercent() {
		return percent;
	}

	public UteBase setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getBase() {
		return base;
	}

	public UteBase setBase(double base) {
		this.base = base;
		return this;
	}

}
