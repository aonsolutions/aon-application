package com.esferalia.aon.occam.api.model.product;

public enum ProductKind {
	
	SALE_PURCHASE,
	
	PURCHASE,
	
	SALE;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
	
}
