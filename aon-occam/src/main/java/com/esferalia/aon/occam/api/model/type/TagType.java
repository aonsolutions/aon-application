package com.esferalia.aon.occam.api.model.type;

public enum TagType {

	RATTACH,
    PRODUCT,
    NOTICE,
    PRIORITY,
    MARKETPLACE;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}