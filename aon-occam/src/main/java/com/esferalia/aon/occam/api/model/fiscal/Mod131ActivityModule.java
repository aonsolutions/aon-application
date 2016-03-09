package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod131ActivityModule implements Serializable {

	private static final long serialVersionUID = 6080727858224392417L;
	
	private String description;
	private double value;
	private String unit;
	private double factor;
	private double result;

	public String getDescription() {
		return description;
	}

	public Mod131ActivityModule setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getValue() {
		return value;
	}

	public Mod131ActivityModule setValue(double value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public Mod131ActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getFactor() {
		return factor;
	}

	public Mod131ActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod131ActivityModule setResult(double result) {
		this.result = result;
		return this;
	}

}
