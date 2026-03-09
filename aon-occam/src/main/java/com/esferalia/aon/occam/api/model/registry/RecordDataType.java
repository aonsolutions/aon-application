package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum RecordDataType implements Serializable {

	INCORPORATION			("Constituci\u00F3n"),
	TRANSFER				("Traslado"),
	COMPANY_NAME_CHANGE		("Modificaci\u00F3n denominaci\u00F3n social"),
	OTHER_REGISTRATIONS		("Otras Inscripciones"),
	;

	private final String description;

	private RecordDataType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) ordinal();
	}

	public static RecordDataType safeValueOf(Byte i) {
		if (i == null) return null;
		return safeValueOf(i.intValue());
	}

	public static RecordDataType safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= RecordDataType.values().length) return null;
		return RecordDataType.values()[i];
	}
	
	public static RecordDataType safeValueOf(String s) {
		if (s == null) return null;
		for (RecordDataType type : RecordDataType.values()) {
			if (s.equalsIgnoreCase(type.name()) || s.equalsIgnoreCase(type.getDescription())) {
				return type;
			}
		}
		return null;
	}

}
