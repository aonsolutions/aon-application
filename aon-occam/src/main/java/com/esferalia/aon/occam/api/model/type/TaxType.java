package com.esferalia.aon.occam.api.model.type;

public enum TaxType {

	UNKNOWN,
    VAT,
    RETENTION;

	public byte value() {
		return (byte) ordinal();
	}
	
}