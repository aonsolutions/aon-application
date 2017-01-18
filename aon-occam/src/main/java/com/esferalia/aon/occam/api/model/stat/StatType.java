package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

public enum StatType implements Serializable {

	INVOICE,
	TASK,
	FEE
	;
	
	
	private StatType() {
	
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}
}
