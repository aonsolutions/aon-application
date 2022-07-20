package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaskStatus {

	DELETED(),
	PENDING,
	IN_PROGRESS,
	FINISHED,
	FAQ,
	DUPLICATE;

	public String getName() {
    	return this.toString().toLowerCase();
    }
    
	public String getGwtName() { // PROVISIONAL!!!!
		if(this.equals(FAQ)) return getName();
		if(this.equals(DELETED)) return getName();
		if(this.equals(PENDING) || this.equals(IN_PROGRESS)) return "open";
		else return "closed";
    }
	
	public String getESName() { // PROVISIONAL!!!!
		if(this.equals(FAQ)) return getName();
		if(this.equals(DELETED)) return "BORRADAS";
		if(this.equals(PENDING) || this.equals(IN_PROGRESS)) return "ABIERTAS";
		else return "CERRADAS";
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TaskStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskStatus.values().length) return null;
		return TaskStatus.values()[i];
	}
	
	public static TaskStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return PENDING;
		for (TaskStatus rs : TaskStatus.values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return TaskStatus.PENDING;
	}
	
}
