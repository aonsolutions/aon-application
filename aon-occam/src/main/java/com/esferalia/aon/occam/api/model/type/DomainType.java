package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DomainType {

	ENTERPRISE,
	CONSULTANCY,
	GARAGE,
	ACADEMY,
	HOTEL,
	ADMIN,
	OFFICE,
	GENERIC,
	COMMERCE,
	KIT_DIGITAL;   
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue() {
		return toString();
	}
	
	public static DomainType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DomainType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DomainType.values().length) return null;
		return DomainType.values()[i];
	}
	
	public static DomainType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (DomainType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
