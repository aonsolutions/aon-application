package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum SalesDetailStatus implements Serializable {

	PENDING,
	PARTIAL_SETTLED,
	SETTLED;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
}
