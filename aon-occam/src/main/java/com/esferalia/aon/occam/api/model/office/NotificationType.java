package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;

public enum NotificationType implements Serializable{
	
	OPEN,
	NEW_INFO,
	CLOSE,
	REOPEN,
	TAG,
	MANUAL;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}
