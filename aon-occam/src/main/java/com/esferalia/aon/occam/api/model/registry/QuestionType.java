package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum QuestionType implements Serializable {
	
	INFO("Informaci\u00f3n"),
    TEXT("Texto"),
    NUMBER("Num\u00e9rico"),
	DATE("Fecha"),
	BOOLEAN("Booleano");
	
	private String description;
	
	private QuestionType(String description) {
		this.description = description;
	}
	
	public String description() {
		return this.description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static QuestionType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static QuestionType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= QuestionType.values().length) return null;
		return QuestionType.values()[i];
	}
	
}
