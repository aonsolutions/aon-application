package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum RawdocStatus implements Serializable {
	
	 INBOX("Inbox")
	,REJECTED("Rechazado")
	,DRAFT("Papelera")
	,PROCESSING("Procesando")
	;

	private String description;
	
	private RawdocStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return this.name().toLowerCase();
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<RawdocStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<RawdocStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RawdocStatus.values().length) return Optional.empty();
		return Optional.of(RawdocStatus.values()[i]);
	}
	
	public static Optional<RawdocStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
}
