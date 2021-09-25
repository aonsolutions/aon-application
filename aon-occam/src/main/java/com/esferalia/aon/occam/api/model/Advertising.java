package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public enum Advertising implements Serializable{

	ALLOWED,
    AUTO_EXCLUSION,
    DENIED,
	ROBINSON;
	
	private Advertising() {
	
	}

	public Byte value(){
		return (byte) ordinal();
	}
	
	public static Advertising safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static Advertising safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Advertising.values().length) return null;
		return Advertising.values()[i];
	}
	
	public static Advertising safeValueOf( String i ) {
		for (Advertising rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
