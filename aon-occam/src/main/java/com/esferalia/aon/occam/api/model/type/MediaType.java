package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MediaType implements Serializable {

	UNKNOWN
	, FIXED_PHONE
	, CELLULAR
	, FAX
	, EMAIL
	, WEB;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}