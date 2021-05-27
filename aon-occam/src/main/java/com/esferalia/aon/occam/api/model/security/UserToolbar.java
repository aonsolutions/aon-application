package com.esferalia.aon.occam.api.model.security;

public enum UserToolbar {

	/**
     * GOOGLE 
     */
	GOOGLE,
	
	/**
     * MICROSOFT 365 
     */
	MICROSOFT_365,
	
	/**
     * HOTMAIL 
     */
	HOTMAIL,

	/**
     * YAHOO 
     */
	YAHOO,

    /**
     * Esferalia WEBMAIL
     */
	ESFERALIA_WEBMAIL,

	/**
     * ARSYS 
     */
	ARSYS,   

	/**
     * ACENS 
     */
	ACENS,

	/**
     * AON SOLUTIONS
     */
	AON_SOLUTIONS

	;   
	
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static UserToolbar safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static UserToolbar safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= UserToolbar.values().length) return null;
		return UserToolbar.values()[i];
	}
}
