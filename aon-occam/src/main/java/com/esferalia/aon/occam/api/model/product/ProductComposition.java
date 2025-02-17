package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public enum ProductComposition implements Serializable {
	
	DIVISIBLE("Pack Divisible"),
    COMPOSITION("Pack Compuesto");
	
	private String description;
	
	private ProductComposition(String description) {
		this.description = description;
	}
	
	public String getDescription() {
	    return this.description;
	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
	public static ProductComposition safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductComposition safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductComposition.values().length) return null;
		return ProductComposition.values()[i];
	}
	
	public static ProductComposition safeValueOf( String i ) {
		if(i == null) return null;
		for (ProductComposition rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
