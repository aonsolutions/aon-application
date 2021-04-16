package com.esferalia.aon.occam.api.model.product;

public enum ProductStatus {
	
	ACTIVE,
    DISCONTINUED;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
	public static ProductStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductStatus.values().length) return null;
		return ProductStatus.values()[i];
	}
	
	public static ProductStatus safeValueOf( String i ) {
		for (ProductStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
