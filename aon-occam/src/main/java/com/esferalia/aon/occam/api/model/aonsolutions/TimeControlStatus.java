package com.esferalia.aon.occam.api.model.aonsolutions;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TimeControlStatus {
	IN,
	OUT,
	PAUSE;
	
	private TimeControlStatus() {
	
	}

	public Byte value() {
		return (byte) ordinal();
	}
	
	public static TimeControlStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TimeControlStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TimeControlStatus.values().length) return null;
		return TimeControlStatus.values()[i];
	}
	
	public static TimeControlStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (TimeControlStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
