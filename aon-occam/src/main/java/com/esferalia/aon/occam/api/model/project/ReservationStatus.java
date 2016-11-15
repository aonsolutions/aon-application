package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public enum ReservationStatus implements Serializable {

	ACTIVE,
	BLOCKED,
	CANCELLED,
	INVOICED;
    
	public String getName() {
		return this.toString();
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
    
}