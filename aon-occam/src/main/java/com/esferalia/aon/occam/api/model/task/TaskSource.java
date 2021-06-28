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
    
    public static TaskSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TaskSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskSource.values().length) return null;
		return TaskSource.values()[i];
	}
}
