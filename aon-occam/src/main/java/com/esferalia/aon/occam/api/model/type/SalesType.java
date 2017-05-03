package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum SalesType implements Serializable {

	NORMAL,
	SAMPLE,
	INTERNET,
	DEVOLUTION;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
}
