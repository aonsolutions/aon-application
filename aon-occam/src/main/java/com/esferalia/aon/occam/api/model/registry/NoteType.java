package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum NoteType implements Serializable {
	

    UNKNOWN,
    OBSERVATION,
    MESSAGE,
    TRACKING,
    FACTURAE;
	
	private NoteType() {
	
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}
