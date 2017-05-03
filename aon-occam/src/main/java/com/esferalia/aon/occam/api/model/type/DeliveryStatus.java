package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DeliveryStatus implements Serializable {
	PENDING,
	INVOICED;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
}
