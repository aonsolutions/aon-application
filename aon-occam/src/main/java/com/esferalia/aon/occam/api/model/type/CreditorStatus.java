package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum CreditorStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	BLOCKED("Bloqueado");
	
	private String description;
	
	private CreditorStatus(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static CreditorStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static CreditorStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CreditorStatus.values().length) return null;
		return CreditorStatus.values()[i];
	}

}