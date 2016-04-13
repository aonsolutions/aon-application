package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum TaxType implements Serializable{

	UNKNOWN,
    VAT,
    RETENTION;

	public byte value() {
		return (byte) ordinal();
	}
	
}