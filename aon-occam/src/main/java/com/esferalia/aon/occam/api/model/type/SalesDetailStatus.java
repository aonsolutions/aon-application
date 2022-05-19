package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum SalesDetailStatus implements Serializable {

	PENDING,
	PARTIAL_SETTLED,
	SETTLED;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static SalesDetailStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SalesDetailStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SalesDetailStatus.values().length) return null;
		return SalesDetailStatus.values()[i];
	}
	
	public static SalesDetailStatus safeValueOf( String i ) {
		for (SalesDetailStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
    public String getName() {
		return this.toString();
    }
    
}
