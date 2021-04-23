package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Administration implements Serializable {
	
	  ALAVA("Araba/Alava")
	, BIZKAIA("Bizkaia")
	, GIPUZKOA("Gipuzkoa")
	, NAVARRA("Navarra")
	, COMMON_TERRITORY("Territorio Com\u00FAn")
	, UNKNOWN("Otro");

	private String description;
	
	private Administration(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte getValue() {
		return (byte) ordinal();
	}
	public static Administration safeValueOf( String i ) {
		if (i == null || AonStringUtils.isBlank(i)) return null;
		return Administration.valueOf( i );
	}
	public static Administration safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static Administration safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Administration.values().length) return null;
		return Administration.values()[i];
	}
}
