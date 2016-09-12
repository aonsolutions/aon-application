package com.esferalia.aon.occam.api.model.task;

public enum TaskStatus {

	OPEN,
	CLOSED;
		
    public String getName() {
    	return this.toString().toLowerCase();
    }
    
    public byte value() {
    	return (byte) this.ordinal();
	}
}
