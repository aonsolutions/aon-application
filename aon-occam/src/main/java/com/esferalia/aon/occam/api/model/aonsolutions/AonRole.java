package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

public enum AonRole  implements Serializable {
	ADMIN,
	GUEST;
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonRole safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}

	public static AonRole safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonRole.values().length) return null;
		return AonRole.values()[i];
	}
	
	public static AonRole safeValueOf( String i ) {
		if (i == null || "".equals(i)) return null;
		if(i.equalsIgnoreCase(ADMIN.name())) {
			return ADMIN;
		} else if(i.equalsIgnoreCase(GUEST.name())) {
			return GUEST;
		}	return null;
	}
}

