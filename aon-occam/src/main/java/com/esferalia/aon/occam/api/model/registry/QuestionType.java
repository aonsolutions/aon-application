package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum QuestionType implements Serializable {
	
	INFO,
    TEXT,
    NUMBER,
	DATE,
	BOOLEAN;
	
	private QuestionType() {
	
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}
