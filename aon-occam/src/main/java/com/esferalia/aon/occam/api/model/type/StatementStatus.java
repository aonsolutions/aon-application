package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum StatementStatus implements Serializable {

	PENDING,
	CHECKED,
	RECORDED;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	public String getName() {
		return this.toString();
	}
}
