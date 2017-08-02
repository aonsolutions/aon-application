package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum VATRegime implements Serializable {

	 GENERAL	("Régimen General"		,"Rég. Genr.")
	,SIMPLIFIED ("Régimen Simplificado"	,"Rég. Simpl.")
	,EXEMPT 	("Exento"				,"Exento")
	;
	
	private String name;
	private String description;
	
	private VATRegime(String name,String description){
		this.name = name;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	
	public static VATRegime safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static VATRegime safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i.intValue() >= VATRegime.values().length) return null;
		return VATRegime.values()[i];
	}

}