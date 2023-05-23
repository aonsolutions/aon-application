package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

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
	
	public static Optional<AonLanguage> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<AonLanguage> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonLanguage.values().length) return Optional.empty();
		return Optional.of( AonLanguage.values()[i]);
	}
	
	public static Optional<AonLanguage> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name())
					|| i.equalsIgnoreCase(dt.getLanguage())
					)
			.findFirst();
	}
}
