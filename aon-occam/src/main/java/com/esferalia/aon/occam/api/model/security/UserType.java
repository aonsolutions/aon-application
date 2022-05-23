package com.esferalia.aon.occam.api.model.security;

public enum UserType {

	NORMAL,
	PORTAL,
	SHARED,
	SERVICE;

	private UserType() {
	
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static UserType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static UserType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= UserType.values().length) return null;
		return UserType.values()[i];
	}
	
	public static UserType safeValueOf( String i ) {
		for (UserType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return UserType.NORMAL;
	}
	
}
