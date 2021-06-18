package com.esferalia.aon.occam.api.model.security;

public enum AuthAttachType {

	AVATAR;

	private AuthAttachType() {
	
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AuthAttachType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AuthAttachType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AuthAttachType.values().length) return null;
		return AuthAttachType.values()[i];
	}
	
	public static AuthAttachType safeValueOf( String i ) {
		for (AuthAttachType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return AuthAttachType.AVATAR;
	}
	
}
