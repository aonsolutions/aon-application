package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public enum InvoiceDetailCommissionStatus implements Serializable {

	PENDING("Pendiente"),
	BLOCKED("Bolqueado"),
	PAID("Pagado");

	String name;
	private InvoiceDetailCommissionStatus(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getName() {	
		return name;
	}

}