package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum AlarmStatus implements Serializable {
	
	PENDING,
	FINISHED,
	READ;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}
