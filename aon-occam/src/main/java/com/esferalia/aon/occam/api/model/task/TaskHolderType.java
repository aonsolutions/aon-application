package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaskHolderType implements Serializable  {

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
    
    public static TaskHolderType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
    
	public static TaskHolderType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskHolderType.values().length) return null;
		return TaskHolderType.values()[i];
	}
	
	public static TaskHolderType safeValueOf(String value) {
		if(AonStringUtils.isBlank(value)) return TaskHolderType.INTERNAL;
		for (TaskHolderType tht : TaskHolderType.values()) {
			if(tht.name().equalsIgnoreCase(value)) {
				return tht;
			}
		}
		return TaskHolderType.INTERNAL;
	}
    
}