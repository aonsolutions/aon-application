package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum FinanceStatus implements Serializable {

	PENDING("Pendiente"),
	BATCHED("Remesado"),
	RETURNED("Devuelto"),
	PAID("Pagado"),
	SETTLED("Saldado");

	private String description;
	
	private FinanceStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static FinanceStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static FinanceStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i > FinanceStatus.values().length) return null;
		return FinanceStatus.values()[i];
	}
}