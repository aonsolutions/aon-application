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

}
