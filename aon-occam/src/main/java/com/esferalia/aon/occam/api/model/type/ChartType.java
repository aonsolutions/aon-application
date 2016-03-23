package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ChartType implements Serializable  {

	PIECHART("Pie"),
	COMBOCHART("Combo");

	private String description;
	
	private ChartType(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
}
