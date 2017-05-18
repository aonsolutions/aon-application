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

}
