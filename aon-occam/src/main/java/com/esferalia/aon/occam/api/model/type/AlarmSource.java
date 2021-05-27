package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum AlarmSource implements Serializable {
	
	NOTICE,
	TASK,
	EXTERNAL,
	COMMERCIAL_TRACKING,
	OFFICE;
	
	public byte value() {
		return (byte) this.ordinal();
	}

}
