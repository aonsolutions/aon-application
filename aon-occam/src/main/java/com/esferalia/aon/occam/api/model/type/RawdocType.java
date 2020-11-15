package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum RawdocType implements Serializable {
	
	 INPUT("Recibido")
	,OUTPUT("Emitido")
	;

	private String description;
	
	private RawdocType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RawdocType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RawdocType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RawdocType.values().length) return null;
		return RawdocType.values()[i];
	}
	
}
