package com.esferalia.aon.occam.api.model;

public enum MailAccountType {

	USER,
	SYSTEM;
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static MailAccountType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}

	public static MailAccountType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= MailAccountType.values().length) return null;
		return MailAccountType.values()[i];
	}
	
	public static MailAccountType safeValueOf( String i ) {
		for (MailAccountType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
