package com.esferalia.aon.occam.api.model.type;

public enum SalesStatus {

    PENDING,
    BLOCKED,
    SERVED,
    CLOSED,
    INVOICED;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
	public static SalesStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SalesStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SalesStatus.values().length) return null;
		return SalesStatus.values()[i];
	}

	public static SalesStatus safeValueOf( String i ) {
		for (SalesStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	

}
