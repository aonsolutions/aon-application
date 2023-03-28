package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonStatus {

	NOT_BILLABLE("No facturable"),
	BILLABLE("Facturable")
	;

	private String name;
	private AonStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	public String getValue() {
		return toString();
	}
	public static AonStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AonStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonStatus.values().length) return null;
		return AonStatus.values()[i];
	}
	
	public static AonStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (AonStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static String getName( AonStatus type) {
		if (type == null) return null;
		return type.getName();
	}
}
