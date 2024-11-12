package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum Mod369PayType implements Serializable {

	 TOTAL 		("Ingreso total", "I")
	,PARTIAL 	("Ingreso parcial", "S")	
	,NO_INCOME 	("Sin ingreso", "O")
	,NEGATIVE 	("Negativa", "N")
	,TRANSFER	("A Ingresar por transferencia", "T")
	;
	
	private String description;
	private String content;
	
	private Mod369PayType(String description, String content) {
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

	public static Mod369PayType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static Mod369PayType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Mod369PayType.values().length) return null;
		return Mod369PayType.values()[i];
	}

}