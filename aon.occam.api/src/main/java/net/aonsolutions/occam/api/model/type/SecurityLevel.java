package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


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
	
	public static byte value(boolean v) {
		return v ? CONFIDENTIAL.value() : OFFICIAL.value();
	}
	
	public static boolean confidential( Byte i ) {
		if (i == null) return false;
		return i.intValue()  == 1; 
	}

	public static Optional<SecurityLevel> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<SecurityLevel> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= SecurityLevel.values().length) return Optional.empty();
		return Optional.of(SecurityLevel.values()[i]);
	}
	
	public static Optional<SecurityLevel> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(SecurityLevel t) {
		return t == null ? null : t.value();
	}
	
}