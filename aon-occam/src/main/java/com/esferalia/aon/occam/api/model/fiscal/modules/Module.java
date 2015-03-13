package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

public class Module implements Serializable {

	private static final long serialVersionUID = 2899951335919767090L;
	
	private int line;
	private String description;
	private String unit;
	private double amount;

	public Module(int line, String description, String unit, double amount) {
		this.line = line;
		this.description = description;
		this.unit = unit;
		this.amount = amount;
	}

	public int getLine() {
		return line;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public double getAmount() {
		return amount;
	}
}
