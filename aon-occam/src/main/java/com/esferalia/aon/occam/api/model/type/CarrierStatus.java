package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum CarrierStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	BLOCKED("Bloqueado");
    
	private String description;

	private CarrierStatus(String description) {
		this.description = description;
	}

	public String getDescription(){
		return this.description;
	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static CarrierStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static CarrierStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CarrierStatus.values().length) return null;
		return CarrierStatus.values()[i];
	}
}