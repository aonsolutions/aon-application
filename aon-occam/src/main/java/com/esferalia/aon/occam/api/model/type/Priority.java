package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Priority implements Serializable {
	NONE,
	LOW,
	NORMAL,
	HIGH
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}
