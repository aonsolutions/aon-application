package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum PurchaseStatus implements Serializable {

	PENDING,
	BLOCKED,
	SERVED,
	CLOSED,
	INVOICED;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
}
