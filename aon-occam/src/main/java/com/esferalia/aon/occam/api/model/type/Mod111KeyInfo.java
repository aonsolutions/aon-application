package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Mod111KeyInfo implements Serializable {
	  NONE("Sin informaci\u00F3n")
	, INVOICE("Ver desglose de retenciones en facturas")
	, SALARY("Ver desglose de retenciones en n\u00F3minas")
	, COMPUTE("Ver desglose de c\u00E1lculos");
	
	private String label;

	private Mod111KeyInfo(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
