package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum OfferStatus implements Serializable {

	PENDING("Pendiente")
	, APPROVED("Aprobado")
	, REFUSED("Rechazado")
	, BLOCKED("Bloqueado")
	, INVOICED("Facturado");

	private String description;

	private OfferStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Byte value() {
		return (byte) ordinal();
	}

}