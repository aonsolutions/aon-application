package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DomainType {

	ENTERPRISE("Empresa"),
	CONSULTANCY("Asesor\u00EDa"),
	GARAGE("Garaje"),
	ACADEMY("Academ\u00EDa"),
	HOTEL("Hotel"),
	ADMIN("Administraci\u00F3n"),
	OFFICE("Despacho"),
	GENERIC("Gen\u00E9rico"),
	COMMERCE("Comercio"),
	KIT_DIGITAL("Kit Digital");

	private String name;
	private DomainType(String name) {
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
	
	public static String getName( DomainType type) {
		if (type == null) return null;
		return type.getName();
	}
}
