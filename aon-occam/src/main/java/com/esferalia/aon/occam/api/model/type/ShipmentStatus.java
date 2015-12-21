package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ShipmentStatus implements Serializable{
	
	PREPARED,
	IN_AGENCY,
	SHIPPING,
	DELIVERED,
	MISSING,
	ERRONEOUS
	;
		
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

}
