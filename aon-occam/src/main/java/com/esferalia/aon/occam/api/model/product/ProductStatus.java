package com.esferalia.aon.occam.api.model.product;

public enum ProductStatus {
	
	ACTIVE,
    DISCONTINUED;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
	
}
