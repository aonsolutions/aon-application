package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Optional;


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
	
	public byte value() {
		return (byte) ordinal();
	}
	
	public static Optional<SecurityLevel> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<SecurityLevel> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= SecurityLevel.values().length) return Optional.empty();
		return Optional.of( SecurityLevel.values()[i]);
	}
	
}