package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum RawdocType implements Serializable {
	
	 INPUT("Recibido")
	,OUTPUT("Emitido")
	;

	private String description;
	
	private RawdocType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<RawdocType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<RawdocType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RawdocType.values().length) return Optional.empty();
		return Optional.of(RawdocType.values()[i]);
	}
	
	public static Optional<RawdocType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
