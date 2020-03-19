package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public enum ProjectStatus implements Serializable {

	PENDING("Pendiente"),
	APPROVED("Aprovado"),
    REFUSED("Rechazado"),
	CLOSED("Cerrado");
    
	String description; 
	private ProjectStatus(String description) {
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
    
	public static ProjectStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProjectStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProjectStatus.values().length) return null;
		return ProjectStatus.values()[i];
	}
}