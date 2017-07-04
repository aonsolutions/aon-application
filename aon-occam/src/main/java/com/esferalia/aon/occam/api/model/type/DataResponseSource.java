package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DataResponseSource implements Serializable{

	QUALITY,
	PROJECT,
	HOTEL,
	SII,
	SII_INVOICE,
	SII_FINANCE;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}