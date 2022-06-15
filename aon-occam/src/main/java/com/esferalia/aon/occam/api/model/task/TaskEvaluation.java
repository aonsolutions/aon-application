package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaskEvaluation {
	VERY_BAD,
	BAD,
	REGULAR,
	GOOD,
	VERY_GOOD
	;

    public byte value() {
    	return (byte) this.ordinal();
	}
    
	public String getName() {
    	return this.toString().toLowerCase();
    }
    
    public static TaskEvaluation safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TaskEvaluation safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskEvaluation.values().length) return null;
		return TaskEvaluation.values()[i];
	}
	
	public static TaskEvaluation safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (TaskEvaluation rs : TaskEvaluation.values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
