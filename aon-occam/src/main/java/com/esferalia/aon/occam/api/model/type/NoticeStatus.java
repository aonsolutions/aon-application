package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum NoticeStatus implements Serializable {
	CLOSED,
	REOPEN,
	OPEN,
	DUPLICATED;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}
