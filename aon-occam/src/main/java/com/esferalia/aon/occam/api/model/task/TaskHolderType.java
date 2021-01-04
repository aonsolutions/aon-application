package com.esferalia.aon.occam.api.model.task;

public enum TaskHolderType  {

	INTERNAL,
	EXTERNAL;
	
	private TaskHolderType() {

	}
	
	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskHolderType valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskHolderType valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
}