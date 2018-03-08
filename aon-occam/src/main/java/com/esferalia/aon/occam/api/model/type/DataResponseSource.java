package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DataResponseSource implements Serializable{

	QUALITY,
	PROJECT,
	HOTEL,
	SII,
	SII_INVOICE,
	SII_FINANCE,
	TEDI_INVOICE,
	SERES_DELIVERY,
	SERES_INVOICE,
	SERES_SALES,
	INGENET,
	PACKING_LIST_NOTIFICATION,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD390
	;


	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static DataResponseSource safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static DataResponseSource safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= DataResponseSource.values().length)
			return null;
		return DataResponseSource.values()[i];
	}
}