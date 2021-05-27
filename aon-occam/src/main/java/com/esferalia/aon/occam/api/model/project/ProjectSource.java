package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public enum ProjectSource implements Serializable {

	CALL_CENTER("Call Center"),
	COMMERCIAL_VISIT("Visita Comercial"),
	PRESCRIPTION("Prescripción"),
	WEB("Web"),
	ADVERTISEMENT("Anuncio"),
	EMAIL("Email"),
	PRESENTATION("Presentación"),
	RECOMMENDATION("Recomendación");
    
	String description;
	
	private ProjectSource(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return this.toString();
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
    
	public static ProjectSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProjectSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProjectSource.values().length) return null;
		return ProjectSource.values()[i];
	}
    
}