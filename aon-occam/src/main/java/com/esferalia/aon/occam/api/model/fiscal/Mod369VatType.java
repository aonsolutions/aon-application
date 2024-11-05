package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum Mod369VatType implements Serializable {

	 STANDARD 	("Estándar", "S")
	,REDUCED 	("Reducido", "R")	
	;
	
	private String description;
	private String content;
	
	private Mod369VatType(String description, String content) {
		this.description = description;
		this.content = content;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getContent() {
		return content;
	}

	public static Mod369VatType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static Mod369VatType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Mod369VatType.values().length) return null;
		return Mod369VatType.values()[i];
	}

}