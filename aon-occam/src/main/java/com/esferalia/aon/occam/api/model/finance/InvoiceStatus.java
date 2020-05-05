package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public enum InvoiceStatus implements Serializable {
	 PENDING("Pendiente")
	,SCORED("Contabilizada")
	,REFUSED("Rechazada")
	,TRASH("Papelera")
	;
	
	private String name;
	
	private InvoiceStatus(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getName() {
		return name;
	}
	
	public static InvoiceStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceStatus.values().length) return null;
		return InvoiceStatus.values()[i];
	}
	
	public static InvoiceStatus safeValueOf(String value) {
		if(value != null) {
			try {
				return valueOf(value);
			} catch (Exception e) {}
		}
		return null;
	}
	
}
