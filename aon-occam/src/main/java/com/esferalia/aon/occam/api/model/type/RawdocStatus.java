package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum RawdocStatus implements Serializable {
	
	 INBOX("Inbox")
	,REJECTED("Rechazado")
	,DRAFT("Papaelera")
	;

	private String description;
	
	private RawdocStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RawdocStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RawdocStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RawdocStatus.values().length) return null;
		return RawdocStatus.values()[i];
	}
	
}
