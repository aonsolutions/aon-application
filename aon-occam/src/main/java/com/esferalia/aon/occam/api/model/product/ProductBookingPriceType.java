package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public enum ProductBookingPriceType implements Serializable {
	
	PVP("P.V.P."),
    PLAN("Precio Plan"),
    FROM("Precio Desde"),
    HIDE("Ocultar Precio")
    ;
	
	private String description;
	
	private ProductBookingPriceType(String description) {
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
    
	public static ProductBookingPriceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductBookingPriceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductBookingPriceType.values().length) return null;
		return ProductBookingPriceType.values()[i];
	}
	
	public static ProductBookingPriceType safeValueOf( String i ) {
		if(i == null) return null;
		for (ProductBookingPriceType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
