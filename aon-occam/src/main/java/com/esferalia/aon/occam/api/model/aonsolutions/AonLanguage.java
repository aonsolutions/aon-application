package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

public enum AonLanguage implements Serializable{
	
	BASQUE("eu"),
	CATALAN("ca"),
	VALENCIAN("va"),
	DEUTSCH("de"),
	ENGLISH("en"),
	GALICIAN("gl"),
	SPANISH("es");
	
	String language;
	
	private AonLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonLanguage safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AonLanguage safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonLanguage.values().length) return null;
		return AonLanguage.values()[i];
	}
	
	public static AonLanguage safeValueOf( String i ) {
		if(i == null) return SPANISH;
		for (AonLanguage rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getLanguage()))
				return rs;
		}
		return SPANISH;
	}
	
	public boolean isBasque() {
		return this == BASQUE;
	}
	
	public boolean isCatalan() {
		return this == CATALAN;
	}
	
	public boolean isValencian() {
		return this == VALENCIAN;
	}
	
	public boolean isDeutsch() {
		return this == DEUTSCH;
	}
	
	public boolean isEnglish() {
		return this == ENGLISH;
	}
	
	public boolean isGalician() {
		return this == GALICIAN;
	}
	
	public boolean isSpanish() {
		return this == SPANISH;
	}
	
	
	
}
