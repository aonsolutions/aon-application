package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public enum OfferDetailCommissionStatus implements Serializable {

	PENDING("Pendiente"),
	BLOCKED("Bloqueado"),
	PAID("Pagado");

	String name;
	private OfferDetailCommissionStatus(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getName() {	
		return name;
	}

}