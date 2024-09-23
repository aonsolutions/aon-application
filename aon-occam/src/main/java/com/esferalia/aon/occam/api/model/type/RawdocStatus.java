package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RawdocStatus implements Serializable {
	
	 INBOX("Inbox", "inbox")
	,REJECTED("Rechazado", "refused")
	,DRAFT("Papelera", "trash")
	,PROCESSING("Procesando", "processing")
	;

	private String description;
	private String tediName;
	
	private RawdocStatus(String description, String tediName) {
		this.description = description;
		this.tediName = tediName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTediName() {
		return tediName;
	}
	
	public String getName() {
		return this.name().toLowerCase();
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
	
	public static RawdocStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (RawdocStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()) || rs.getTediName().equalsIgnoreCase(i))
				return rs;
		}
		return null;
	}
}
