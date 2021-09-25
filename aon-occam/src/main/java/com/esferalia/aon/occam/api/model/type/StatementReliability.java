package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum StatementReliability implements Serializable{
	
	VERY_HIGH,
	HIGH,
	MEDIUM,
	LOW;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName() {
		return this.toString();
	}
}
