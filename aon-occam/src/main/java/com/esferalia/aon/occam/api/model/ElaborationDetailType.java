package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ElaborationDetailType {

	ELABORATION,
	PACKAGING;
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static ElaborationDetailType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ElaborationDetailType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ElaborationDetailType.values().length) return null;
		return ElaborationDetailType.values()[i];
	}
	
	public static ElaborationDetailType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ElaborationDetailType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
