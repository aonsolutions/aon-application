package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;

public enum InvoiceDetailCommissionStatus implements Serializable {

	PENDING("Pendiente"),
	BLOCKED("Bloqueado"),
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

	public static InvoiceDetailCommissionStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceDetailCommissionStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceDetailCommissionStatus.values().length) return null;
		return InvoiceDetailCommissionStatus.values()[i];
	}
	
}