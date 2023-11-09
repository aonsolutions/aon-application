package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum DeliveryCommunicationStatus implements Serializable{
	
	PENDING,
	ACCEPTED,
	WRONG;
	
	
	private DeliveryCommunicationStatus() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static DeliveryCommunicationStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static DeliveryCommunicationStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DeliveryCommunicationStatus.values().length) return null;
		return DeliveryCommunicationStatus.values()[i];
	}
	
	public static DeliveryCommunicationStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (DeliveryCommunicationStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
