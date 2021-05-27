package com.esferalia.aon.occam.api.model.product;

public enum ProductKind {
	
	SALE_PURCHASE,
	
	PURCHASE,
	
	SALE;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
	public static ProductKind safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductKind safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductKind.values().length) return null;
		return ProductKind.values()[i];
	}
	
	public static ProductKind safeValueOf( String i ) {
		for (ProductKind rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
