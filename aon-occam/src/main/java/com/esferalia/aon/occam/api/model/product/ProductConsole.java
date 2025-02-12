package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public enum ProductConsole implements Serializable {
	
	SELF_CONTRACT,
    CONSOLE;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
	public static ProductConsole safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductConsole safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductConsole.values().length) return null;
		return ProductConsole.values()[i];
	}
	
	public static ProductConsole safeValueOf( String i ) {
		if(i == null) return null;
		for (ProductConsole rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
