package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public enum Status implements Serializable {
	
	ACTIVE("Activo"),
    INACTIVE("Inactivo");
	
	private String description;
	
	private Status(String description) {
		this.description = description;
	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
    public String getDescription() {
	    return this.description;
	}
    
	public static Status safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static Status safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Status.values().length) return null;
		return Status.values()[i];
	}
	
	public static Status safeValueOf( String i ) {
		if(i == null) return null;
		for (Status rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
