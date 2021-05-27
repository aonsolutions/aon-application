package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum IRPFRegime implements Serializable {

	 NORMAL 	("Estimaci\u00F3n directa normal"		,"Est. Normal")
	,SIMPLIFIED ("Estimaci\u00F3n directa simplificada"	,"Est. Simpl.")
	,OBJECTIVE 	("Estimaci\u00F3n objetiva"				,"Est. Objet.")	
	,EXEMPT 	("Exento"								,"Exento")
	;
	
	private String name;
	private String description;
	
	private IRPFRegime(String name,String description){
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
	public static IRPFRegime safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static IRPFRegime safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i.intValue() >= IRPFRegime.values().length) return null;
		return IRPFRegime.values()[i];
	}

}