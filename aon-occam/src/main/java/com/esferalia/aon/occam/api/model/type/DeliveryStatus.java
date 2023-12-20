package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DeliveryStatus implements Serializable {
	PENDING,
	INVOICED,
	IN_PREPARATION;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
	public static DeliveryStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DeliveryStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DeliveryStatus.values().length) return null;
		return DeliveryStatus.values()[i];
	}

	public static DeliveryStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (DeliveryStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
