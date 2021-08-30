package com.esferalia.aon.occam.api.model.type;

public enum SellerStatus {
	
    ACTIVE,
    INACTIVE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static SellerStatus safeValueOf( Byte i ) {
		if (i == null) return ACTIVE;
		return safeValueOf( i.intValue() ); 
	}

	public static SellerStatus safeValueOf( Integer i ) {
		if (i == null) return ACTIVE;
		if (i < 0 || i >= SellerStatus.values().length) return null;
		return SellerStatus.values()[i];
	}
	
	public static SellerStatus safeValueOf( String i ) {
		for (SellerStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return ACTIVE;
	}

}
