package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum InvoiceType implements Serializable  {

	PURCHASE("Compras"),
	SALES("Ventas"),
	EXPENSES("Gastos"),
	UNDEDUCTIBLE("Gt.NO Ded");

	private String description;
	
	private InvoiceType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
