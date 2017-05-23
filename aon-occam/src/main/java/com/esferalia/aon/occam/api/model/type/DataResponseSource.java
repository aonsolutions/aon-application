package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DataResponseSource implements Serializable{

	QUALITY,
	PROJECT;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}