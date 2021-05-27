package com.esferalia.aon.occam.api.model.type;

public enum MaritalStatus {

	/** SINGLE. */
    SINGLE,

    /** MARRIED. */
    MARRIED,

    /** DIVORCED. */
    DIVORCED,

    /** SEPARATED. */
    SEPARATED,

    /** WIDOWED. */
    WIDOWED,

    /** UNKNOWN. */
    UNKNOWN;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
	public static MaritalStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static MaritalStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= MaritalStatus.values().length) return null;
		return MaritalStatus.values()[i];
	}

}
