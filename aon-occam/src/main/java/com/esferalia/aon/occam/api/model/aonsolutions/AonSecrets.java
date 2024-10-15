package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonSecrets implements Serializable {

	AON_SECRET("aonsolutions/aonsecret");
	
	String description;
	
	private AonSecrets(String description) {
		this.description = description;
	}
	
	public String getName() {
		return this.name();
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonSecrets safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AonSecrets safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonSecrets.values().length) return null;
		return AonSecrets.values()[i];
	}
	
	public static AonSecrets safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (AonSecrets rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static List<AonSecrets> getValues() {
		return Arrays.asList(values());
	}
}
