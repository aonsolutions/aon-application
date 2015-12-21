package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum CustomerStatus implements Serializable {

	ACTIVE,
	INACTIVE,
    BLOCKED;
    
	public String getName(){
		return this.toString();
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
}