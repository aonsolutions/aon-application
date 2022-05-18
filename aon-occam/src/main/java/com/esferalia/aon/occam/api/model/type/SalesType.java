package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum SalesType implements Serializable {

	NORMAL,
	SAMPLE,
	INTERNET,
	DEVOLUTION;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static SalesType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SalesType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SalesType.values().length) return null;
		return SalesType.values()[i];
	}
	
	public static SalesType safeValueOf( String i ) {
		for (SalesType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
    
}
