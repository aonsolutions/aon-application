package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MediaType implements Serializable {

	UNKNOWN
	, FIXED_PHONE
	, CELLULAR
	, FAX
	, EMAIL
	, WEB;
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static MediaType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static MediaType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= MediaType.values().length) return null;
		return MediaType.values()[i];
	}
	
	
}