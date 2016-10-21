package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum SupplierStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	BLOCKED("Bloqueado");
	
	private String description;
	
	private SupplierStatus(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static SupplierStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static SupplierStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SupplierStatus.values().length) return null;
		return SupplierStatus.values()[i];
	}

}