package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Administration implements Serializable {
	
	  ALAVA("Araba/Alava")
	, BIZKAIA("Bizkaia")
	, GIPUZKOA("Gipuzkoa")
	, NAVARRA("Navarra")
	, COMMON_TERRITORY("Territorio Com\u00FAn")
	, UNKNOWN("Otro");

	private String description;
	
	private Administration(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte getValue() {
		return (byte) ordinal();
	}
	
}
