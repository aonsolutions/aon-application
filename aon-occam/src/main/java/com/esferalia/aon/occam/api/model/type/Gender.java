package com.esferalia.aon.occam.api.model.type;

public enum Gender {

    /** MALE. */
    MALE,

    /** FEMALE. */
    FEMALE,

    /** UNKNOWN. */
    UNKNOWN;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static Gender safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static Gender safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Gender.values().length) return null;
		return Gender.values()[i];
	}
}
