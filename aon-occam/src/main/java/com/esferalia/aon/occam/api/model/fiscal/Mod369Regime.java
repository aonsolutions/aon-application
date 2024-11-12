package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum Mod369Regime implements Serializable {

	 UNION 		("R\u00E9gimen de la Uni\u00F3n", "MOSS")
	,OUTSIDE 	("R\u00E9gimen Exterior a la Uni\u00F3n", "VOES")	
	,IMPORT 	("R\u00E9gimen de Importaci\u00F3n", "IMPO")
	;
	
	private String description;
	private String content;
	
	private Mod369Regime(String description, String content) {
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

	public static Mod369Regime safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static Mod369Regime safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Mod369Regime.values().length) return null;
		return Mod369Regime.values()[i];
	}

}