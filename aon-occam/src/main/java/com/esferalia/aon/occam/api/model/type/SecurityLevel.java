package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum SecurityLevel implements Serializable{

	OFFICIAL("NO confidencial"),
	CONFIDENTIAL("Confidencial");

	private String name;
	private SecurityLevel(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public Byte value() {
		return (byte) ordinal();
	}
	public static SecurityLevel safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static SecurityLevel safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SecurityLevel.values().length) return null;
		return SecurityLevel.values()[i];
	}
	
	public static SecurityLevel safeValueOf( String i ) {
		if(i == null) return null;
		for (SecurityLevel rs : values()) {
			if(rs.name().equalsIgnoreCase(i) || rs.getName().equalsIgnoreCase(i))
				return rs;
		}
		return null;
	}
}