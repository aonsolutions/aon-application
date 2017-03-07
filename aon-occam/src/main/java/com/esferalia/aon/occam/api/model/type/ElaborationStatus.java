package com.esferalia.aon.occam.api.model.type;

public enum ElaborationStatus {

    PENDING,
    CLOSED,
    IN_PROGRESS,
    FAIL,
    ;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

}
