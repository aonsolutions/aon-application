package com.esferalia.aon.occam.api.model.security;

public enum DeviceType {
	WEB,
	ANDROID, 
	IPHONE,
	IPAD,
	IPOD,
	BLACKBERRY
	;
	
	private DeviceType() {}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static DeviceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DeviceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DeviceType.values().length) return null;
		return DeviceType.values()[i];
	}
	
	public static DeviceType safeValueOf( String i ) {
		for (DeviceType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return DeviceType.WEB;
	}
}
