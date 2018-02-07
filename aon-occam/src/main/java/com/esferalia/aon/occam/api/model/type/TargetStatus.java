package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum TargetStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo");
	
	private String description;
	
	private TargetStatus(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static TargetStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static TargetStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TargetStatus.values().length) return null;
		return TargetStatus.values()[i];
	}

}