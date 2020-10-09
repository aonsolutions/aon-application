package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;


public enum DataAttachSource implements Serializable {
	
	QUALITY,
	INVOICE,
	FBATCH,
	PRODUCTION,
	DELIVERY,
	SII,
	SERES,
	INGENET,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	IMPORTATION
	;

	public byte value() {
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static DataAttachSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static DataAttachSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= DataAttachSource.values().length) return null;
		return DataAttachSource.values()[i];
	}
	
}