package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum SalesInfoType implements Serializable{

	INGENET;
	
	private SalesInfoType() {

	}
		
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static SalesInfoType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SalesInfoType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SalesInfoType.values().length) return null;
		return SalesInfoType.values()[i];
	}
	
	public static SalesInfoType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (SalesInfoType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
