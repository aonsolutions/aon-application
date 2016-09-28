package com.esferalia.aon.occam.api.model.task;

public enum TaskSource {

	MANUAL,
	ASSIGNED,
	PROCESS,
	CAU;

	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
}
