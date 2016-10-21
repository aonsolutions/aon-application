package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum CustomerStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	BLOCKED("Bloqueado");
    
	private String description;

	private CustomerStatus(String description) {
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
	
	public static CustomerStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static CustomerStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CustomerStatus.values().length) return null;
		return CustomerStatus.values()[i];
	}
}