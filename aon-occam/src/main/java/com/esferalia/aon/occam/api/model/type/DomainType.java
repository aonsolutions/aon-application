package com.esferalia.aon.occam.api.model.type;

public enum DomainType {

	ENTERPRISE,
	CONSULTANCY,
	GARAGE,
	ACADEMY,
	HOTEL,
	ADMIN,
	OFFICE,
	GENERIC;   
	
	
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

}
