package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DataRequestType implements Serializable{

	SII,
	TBAI,
	LROE;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static DataRequestType safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static DataRequestType safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= DataRequestType.values().length)
			return null;
		return DataRequestType.values()[i];
	}
}