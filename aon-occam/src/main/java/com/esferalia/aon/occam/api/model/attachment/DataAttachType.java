package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;


public enum DataAttachType implements Serializable {
	
	REQUEST,
	RESPONSE_OK,
	RESPONSE_ERROR;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static DataAttachType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static DataAttachType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DataAttachType.values().length) return null;
		return DataAttachType.values()[i];
	}
	
	public static Byte[] drive(){
		return new Byte[]{
			REQUEST.value(), RESPONSE_OK.value(),
			RESPONSE_ERROR.value()
		};
	}
}