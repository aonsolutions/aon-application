package com.esferalia.aon.occam.api.model.task;

public enum TaskSource {

	MANUAL,
	ASSIGNED,
	PROCESS,
	CAU,
	GITHUB;

	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskSource valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskSource valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
}
