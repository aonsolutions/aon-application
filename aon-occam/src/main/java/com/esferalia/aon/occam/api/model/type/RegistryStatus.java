package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum RegistryStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	BLOCKED("Bloqueado");
    
	private String description;

	private RegistryStatus(String description) {
		this.description = description;
	}

	public String getDescription(){
		return this.description;
	}
	
	@Deprecated
	public String getName(){
		return this.toString();
	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static RegistryStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RegistryStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistryStatus.values().length) return null;
		return RegistryStatus.values()[i];
	}
}