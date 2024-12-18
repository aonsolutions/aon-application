package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Advertising implements Serializable{

	ALLOWED("Autorizado"),
    AUTO_EXCLUSION("Auto Excluido"),
    DENIED("Denegado"),
	ROBINSON("Robinson");
	
	private String description;
	
	private Advertising(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
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
		if(AonStringUtils.isBlank(i)) return null;
		for (Advertising rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
