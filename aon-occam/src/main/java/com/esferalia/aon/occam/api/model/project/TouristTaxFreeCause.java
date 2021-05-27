package com.esferalia.aon.occam.api.model.project;

public enum TouristTaxFreeCause {

	FORCE_MAJEURE,
	HEALTH,
	PUBLIC_ADMINISTRATION_SOCIAL_PROGRAM;
    
	public Byte value() {
		return (byte) this.ordinal();
	}   
    
}
