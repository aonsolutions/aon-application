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

	public static OfferDetailCommissionStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static OfferDetailCommissionStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= OfferDetailCommissionStatus.values().length) return null;
		return OfferDetailCommissionStatus.values()[i];
	}
}