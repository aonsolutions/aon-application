package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ShipmentStatus implements Serializable{
	
	PREPARED,
	IN_AGENCY,
	SHIPPING,
	DELIVERED,
	MISSING,
	ERRONEOUS
	;
		
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static ShipmentStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ShipmentStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ShipmentStatus.values().length) return null;
		return ShipmentStatus.values()[i];
	}
	
	public static ShipmentStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ShipmentStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
