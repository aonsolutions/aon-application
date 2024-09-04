package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaskHolderWorkgroupType  {

	USER,
	ADMIN;
	
	private TaskHolderWorkgroupType() {

	}
	
	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskHolderWorkgroupType valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskHolderWorkgroupType valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskHolderWorkgroupType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
    
	public static TaskHolderWorkgroupType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskHolderWorkgroupType.values().length) return null;
		return TaskHolderWorkgroupType.values()[i];
	}
	
	public static TaskHolderWorkgroupType safeValueOf(String value) {
		if(AonStringUtils.isBlank(value)) return TaskHolderWorkgroupType.USER;
		for (TaskHolderWorkgroupType tht : TaskHolderWorkgroupType.values()) {
			if(tht.name().equalsIgnoreCase(value)) {
				return tht;
			}
		}
		return TaskHolderWorkgroupType.USER;
	}
    
}