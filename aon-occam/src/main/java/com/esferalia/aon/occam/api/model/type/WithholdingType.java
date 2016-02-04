package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum WithholdingType implements Serializable {

	 PROFESSIONAL("Profesional")
	,RENTING("Arrendamiento")
	,MOVABLE_CAPITAL("Cap. Mobiliario")
	,FARMER("Agricultura")
	,TRANSPORT_OPERATOR("Transpor. y Asim.")	
	;
	private String description;
	
	private WithholdingType(String description){
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	

}