package com.esferalia.aon.occam.api.model.type;

public enum SalesStatus {

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
