package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public enum ProductBookingType implements Serializable {
	
	PLAN("Plan"),
    SERVICE("Servicio"),
    USER("Usuario"),
    CONSULTANCY("Asesoria"),
    DEFAULT("Inicial")
    ;
	
	private String description;
	
	private ProductBookingType(String description) {
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
    
	public static ProductBookingType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductBookingType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductBookingType.values().length) return null;
		return ProductBookingType.values()[i];
	}
	
	public static ProductBookingType safeValueOf( String i ) {
		if(i == null) return null;
		for (ProductBookingType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
