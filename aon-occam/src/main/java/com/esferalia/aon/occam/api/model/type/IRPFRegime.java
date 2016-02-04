package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum IRPFRegime implements Serializable {

	 NORMAL ("Estimaci\u00F3n directa normal"			,"Est. Normal")
	,SIMPLIFIED ("Estimaci\u00F3n directa simplificada"	,"Est. Simpl.")
	,OBJECTIVE ("Estimaci\u00F3n objetiva"				,"Est. Objet.")	
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

}