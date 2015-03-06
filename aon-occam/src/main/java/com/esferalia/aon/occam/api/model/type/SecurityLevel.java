package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum SecurityLevel implements Serializable{

	OFFICIAL,
	CONFIDENTIAL;

	public Byte value() {
		return (byte) ordinal();
	}
	
}