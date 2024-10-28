package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
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

	public static Optional<DomainType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<DomainType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= DomainType.values().length) return Optional.empty();
		return Optional.of(DomainType.values()[i]);
	}
	
	public static Optional<DomainType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public static String value(DomainType t) {
		return t == null ? null : t.name();
	}
	
}
