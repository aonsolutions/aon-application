package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DeliveryCommunicationType implements Serializable{
	
	SERES;
	
	private DeliveryCommunicationType() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static DeliveryCommunicationType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DeliveryCommunicationType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DeliveryCommunicationType.values().length) return null;
		return DeliveryCommunicationType.values()[i];
	}
	
	public static DeliveryCommunicationType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (DeliveryCommunicationType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
