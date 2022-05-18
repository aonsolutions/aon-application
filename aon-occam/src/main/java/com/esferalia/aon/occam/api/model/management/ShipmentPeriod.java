package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;

public enum ShipmentPeriod implements Serializable{
	
	IN_COMMENTS,
	MORNING,	
	NOON,
	AFTERNOON,
	BEFORE_10,
	BEFORE_12,
	OFFICE_HOURS,
	AFTER_19 
	;
	
	private ShipmentPeriod() {
		
	}

	public Byte value(){
		return (byte) ordinal();
	}
	
	public static ShipmentPeriod safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ShipmentPeriod safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ShipmentPeriod.values().length) return null;
		return ShipmentPeriod.values()[i];
	}
	
	public static ShipmentPeriod safeValueOf( String i ) {
		for (ShipmentPeriod rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
